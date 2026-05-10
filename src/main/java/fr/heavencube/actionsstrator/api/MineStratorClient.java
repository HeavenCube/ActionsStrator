package fr.heavencube.actionsstrator.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MineStratorClient {

    private static final String BASE_URL = "https://mine.sttr.io/";

    private final String apiKey;
    private final String serverId;
    private final String userId;
    private final String myboxId;
    private final HttpClient httpClient;
    private final Logger logger;
    private final Gson gson;
    private WebSocket webSocket;

    public MineStratorClient(String apiKey, String serverId, String userId, String myboxId, Logger logger) {
        this.apiKey = apiKey;
        this.serverId = serverId;
        this.userId = userId;
        this.myboxId = myboxId;
        this.logger = logger;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    private boolean isConfigured() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            logger.warning("MineStrator API Key is not configured properly!");
            return false;
        }
        return true;
    }

    public CompletableFuture<Boolean> sendPowerAction(String poweraction) {
        if (!isConfigured())
            return CompletableFuture.completedFuture(false);
        if (serverId == null || serverId.isEmpty() || serverId.equals("YOUR_SERVER_ID_HERE")) {
            logger.warning("MineStrator Server ID is not configured properly!");
            return CompletableFuture.completedFuture(false);
        }

        JsonObject body = new JsonObject();
        body.addProperty("poweraction", poweraction);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "server/" + serverId + "/poweraction"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return true;
                    } else {
                        logger.warning(
                                "Failed to execute MineStrator API request. HTTP Status: " + response.statusCode());
                        logger.warning("Response: " + response.body());
                        return false;
                    }
                })
                .exceptionally(ex -> {
                    logger.severe("An error occurred while communicating with MineStrator API: " + ex.getMessage());
                    return false;
                });
    }

    public CompletableFuture<Boolean> stopServer() {
        return sendPowerAction("stop");
    }

    public CompletableFuture<Boolean> restartServer() {
        return sendPowerAction("restart");
    }

    public CompletableFuture<JsonObject> createServer(String name, int cpu, int ram, int location, int idEgg) {
        if (!isConfigured())
            return CompletableFuture.completedFuture(null);
        if (myboxId == null || myboxId.isEmpty() || myboxId.equals("YOUR_MYBOX_ID_HERE")) {
            logger.warning("MineStrator MyBox ID is not configured properly!");
            return CompletableFuture.completedFuture(null);
        }

        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("cpu", cpu);
        body.addProperty("ram", ram);
        body.addProperty("location", location);
        body.addProperty("id_egg", idEgg);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "mybox/" + myboxId + "/server"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return jsonResponse;
                    } else {
                        logger.warning("Failed to create server. HTTP Status: " + response.statusCode());
                        logger.warning("Response: " + response.body());
                        return jsonResponse;
                    }
                })
                .exceptionally(ex -> {
                    logger.severe("An error occurred while creating server: " + ex.getMessage());
                    return null;
                });
    }

    public void connectToConsole() {
        if (!isConfigured())
            return;
        if (userId == null || userId.isEmpty() || userId.equals("YOUR_USER_ID_HERE")) {
            logger.warning("MineStrator User ID is not configured properly for WebSocket.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "user/" + userId + "/servers/websocket"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                        JsonObject api = root.getAsJsonObject("api");
                        if (api != null && api.has("data")) {
                            JsonObject data = api.getAsJsonObject("data");
                            JsonArray servers = data.getAsJsonArray("servers");
                            for (int i = 0; i < servers.size(); i++) {
                                JsonObject srv = servers.get(i).getAsJsonObject();
                                if (srv.get("id").getAsString().equals(serverId)) {
                                    String socketUrl = srv.get("socket").getAsString();
                                    String token = srv.get("token").getAsString();
                                    logger.info("Connecting to MineStrator WebSocket at URL: " + socketUrl);
                                    connectWebSocket(socketUrl, token);
                                    return;
                                }
                            }
                            logger.warning("Server ID " + serverId + " not found in WebSocket response.");
                        }
                    } else {
                        logger.warning("Failed to get WebSocket token. HTTP Status: " + response.statusCode());
                        logger.warning("Response: " + response.body());
                    }
                })
                .exceptionally(ex -> {
                    logger.severe("An error occurred while fetching WebSocket token: " + ex.getMessage());
                    return null;
                });
    }

    private void connectWebSocket(String socketUrl, String token) {
        httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(socketUrl), new ConsoleWebSocketListener(token))
                .thenAccept(ws -> {
                    this.webSocket = ws;
                })
                .exceptionally(ex -> {
                    logger.severe("Failed to connect to WebSocket: " + ex.getMessage());
                    return null;
                });
    }

    private void sendAuth(WebSocket ws, String token) {
        JsonObject authEvent = new JsonObject();
        authEvent.addProperty("event", "auth");
        JsonArray args = new JsonArray();
        args.add(token);
        authEvent.add("args", args);
        ws.sendText(gson.toJson(authEvent), true);
    }

    private class ConsoleWebSocketListener implements WebSocket.Listener {
        private String currentToken;

        public ConsoleWebSocketListener(String token) {
            this.currentToken = token;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            sendAuth(webSocket, currentToken);
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            try {
                JsonObject message = JsonParser.parseString(data.toString()).getAsJsonObject();
                if (message.has("event")) {
                    String event = message.get("event").getAsString();

                    if (event.equals("auth success")) {
                        logger.info("Successfully authenticated to MineStrator WebSocket console!");
                        // Request old logs
                        JsonObject getLogs = new JsonObject();
                        getLogs.addProperty("event", "send logs");
                        getLogs.add("args", new JsonArray());
                        webSocket.sendText(gson.toJson(getLogs), true);
                    } else if (event.equals("token expiring")) {
                        logger.info("WebSocket token is expiring, re-fetching token...");
                        reAuthenticate(webSocket);
                    } else if (event.equals("console output")) {
                        JsonArray args = message.getAsJsonArray("args");
                        if (args != null && args.size() > 0) {
                            String log = args.get(0).getAsString();
                            logger.info("[MineStrator] " + log);
                        }
                    } else if (event.equals("status")) {
                        JsonArray args = message.getAsJsonArray("args");
                        if (args != null && args.size() > 0) {
                            logger.info("MineStrator Server Status changed: " + args.get(0).getAsString());
                        }
                    } else if (event.equals("stats")) {
                        JsonArray args = message.getAsJsonArray("args");
                        if (args != null && args.size() > 0) {
                            String statsStr = args.get(0).getAsString();
                            JsonObject stats = JsonParser.parseString(statsStr).getAsJsonObject();
                            long memoryBytes = stats.get("memory_bytes").getAsLong();
                            double cpu = stats.get("cpu_absolute").getAsDouble();
                            String state = stats.get("state").getAsString();
                            // Left undocumented logic to process stats
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors silently to not spam if unhandled formats arrive
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            logger.severe("MineStrator WebSocket error: " + error.getMessage());
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logger.info("MineStrator WebSocket closed: " + reason);
            return null;
        }

        private void reAuthenticate(WebSocket ws) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "user/" + userId + "/servers/websocket"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                            JsonObject api = root.getAsJsonObject("api");
                            if (api != null && api.has("data")) {
                                JsonObject data = api.getAsJsonObject("data");
                                JsonArray servers = data.getAsJsonArray("servers");
                                for (int i = 0; i < servers.size(); i++) {
                                    JsonObject srv = servers.get(i).getAsJsonObject();
                                    if (srv.get("id").getAsString().equals(serverId)) {
                                        this.currentToken = srv.get("token").getAsString();
                                        sendAuth(ws, this.currentToken);
                                        return;
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
