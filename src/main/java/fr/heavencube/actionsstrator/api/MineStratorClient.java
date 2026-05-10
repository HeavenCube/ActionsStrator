package fr.heavencube.actionsstrator.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MineStratorClient {

    private static final String BASE_URL = "https://mine.sttr.io/";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final String apiKey;
    private final String serverId;
    private final HttpClient httpClient;
    private final Logger logger;
    private final Gson gson;
    private ServerInfo cachedServerInfo;

    public MineStratorClient(String apiKey, String serverId, Logger logger) {
        this.apiKey = apiKey;
        this.serverId = serverId;
        this.logger = logger;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
                .build();
        this.gson = new Gson();
        this.cachedServerInfo = null;
    }

    private boolean isConfigured() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            logger.warning("MineStrator API Key is not configured properly!");
            return false;
        }
        return true;
    }

    private boolean isServerConfigured() {
        if (serverId == null || serverId.isEmpty() || serverId.equals("YOUR_SERVER_ID_HERE")) {
            logger.warning("MineStrator Server ID is not configured properly!");
            return false;
        }
        return true;
    }

    private HttpRequest.Builder requestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(TIMEOUT)
                .header("Authorization", "Bearer " + apiKey);
    }

    private CompletableFuture<Boolean> handleBooleanResponse(CompletableFuture<HttpResponse<String>> responseFuture, String errorPrefix) {
        return responseFuture
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return true;
                    }

                    logger.warning(errorPrefix + response.statusCode());
                    logger.warning("Response: " + response.body());
                    return false;
                })
                .exceptionally(ex -> {
                    logger.severe("An error occurred while communicating with MineStrator API: " + ex.getMessage());
                    return false;
                });
    }

    private String getNestedString(JsonObject parent, String... path) {
        JsonObject current = parent;

        for (int i = 0; i < path.length; i++) {
            String key = path[i];

            if (current == null || !current.has(key) || current.get(key).isJsonNull()) {
                return "Unknown";
            }

            if (i == path.length - 1) {
                return current.get(key).getAsString();
            }

            if (!current.get(key).isJsonObject()) {
                return "Unknown";
            }

            current = current.getAsJsonObject(key);
        }

        return "Unknown";
    }

    public CompletableFuture<Boolean> sendPowerAction(String poweraction) {
        if (!isConfigured())
            return CompletableFuture.completedFuture(false);
        if (!isServerConfigured()) {
            return CompletableFuture.completedFuture(false);
        }

        JsonObject body = new JsonObject();
        body.addProperty("poweraction", poweraction);

        HttpRequest request = requestBuilder("server/" + serverId + "/poweraction")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        return handleBooleanResponse(httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()),
                "Failed to execute MineStrator API request. HTTP Status: ");
    }

    public CompletableFuture<ServerInfo> getServerInfo() {
        if (!isConfigured())
            return CompletableFuture.completedFuture(null);
        if (!isServerConfigured()) {
            return CompletableFuture.completedFuture(null);
        }

        HttpRequest request = requestBuilder("server/" + serverId)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        try {
                            JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                            JsonObject data = json.getAsJsonObject("api").getAsJsonObject("data");

                            String myboxName = getNestedString(data, "mybox", "name");
                            String offerName = getNestedString(data, "offer", "name");
                            String serverName = getNestedString(data, "server", "name");
                            
                            JsonObject powerActionMessages = new JsonObject();
                            if (data.has("settings") && data.getAsJsonObject("settings").has("poweraction_message")) {
                                powerActionMessages = data.getAsJsonObject("settings").getAsJsonObject("poweraction_message");
                            }

                            return new ServerInfo(myboxName, offerName, serverName, powerActionMessages);
                        } catch (Exception e) {
                            logger.warning("Failed to parse server info response: " + e.getMessage());
                            return null;
                        }
                    } else {
                        logger.warning("Failed to fetch server info. HTTP Status: " + response.statusCode());
                        return null;
                    }
                })
                .thenApply(serverInfo -> {
                    this.cachedServerInfo = serverInfo;
                    return serverInfo;
                })
                .exceptionally(ex -> {
                    logger.severe("An error occurred while fetching server info: " + ex.getMessage());
                    return null;
                });
    }

    public boolean isActionAvailable(String action) {
        // Actions: restart, restart10, stop, stop10, kill
        // Since we don't have live serverInfo here, we rely on the try-catch from sendPowerAction
        // This is a basic check - a 404 or timeout indicates the action isn't configured
        return true; // Will be caught by sendPowerAction failure
    }

    public boolean isActionConfigured(String actionType) {
        if (cachedServerInfo == null) {
            return true; // If we don't have cached info, assume available (will fail with proper error if not)
        }
        
        // Map action types to their base names for the API
        // e.g. "restart10" maps to "restart", "stop10" maps to "stop"
        String baseActionType = actionType.replaceAll("10$", "");
        
        return cachedServerInfo.isActionConfigured(baseActionType);
    }

    public static class ServerInfo {
        public final String myboxName;
        public final String offerName;
        public final String serverName;
        public final JsonObject powerActionMessages;

        public ServerInfo(String myboxName, String offerName, String serverName, JsonObject powerActionMessages) {
            this.myboxName = myboxName;
            this.offerName = offerName;
            this.serverName = serverName;
            this.powerActionMessages = powerActionMessages;
        }
        
        public boolean isActionConfigured(String actionType) {
            if (powerActionMessages == null || !powerActionMessages.has(actionType)) {
                return false;
            }
            String message = powerActionMessages.get(actionType).getAsString();
            return message != null && !message.trim().isEmpty();
        }
    }
}
