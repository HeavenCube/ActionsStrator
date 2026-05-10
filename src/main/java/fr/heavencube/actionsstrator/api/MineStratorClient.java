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

    private final String apiKey;
    private final String serverId;
    private final HttpClient httpClient;
    private final Logger logger;
    private final Gson gson;

    public MineStratorClient(String apiKey, String serverId, Logger logger) {
        this.apiKey = apiKey;
        this.serverId = serverId;
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
}
