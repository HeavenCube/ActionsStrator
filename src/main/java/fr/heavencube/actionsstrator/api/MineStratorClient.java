package fr.heavencube.actionsstrator.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MineStratorClient {

    private static final String BASE_URL = "https://mine.sttr.io/server/";

    private final String apiKey;
    private final String serverId;
    private final HttpClient httpClient;
    private final Logger logger;

    public MineStratorClient(String apiKey, String serverId, Logger logger) {
        this.apiKey = apiKey;
        this.serverId = serverId;
        this.logger = logger;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public CompletableFuture<Boolean> sendCommand(String command) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            logger.warning("MineStrator API Key is not configured properly!");
            return CompletableFuture.completedFuture(false);
        }

        if (serverId == null || serverId.isEmpty() || serverId.equals("YOUR_SERVER_ID_HERE")) {
            logger.warning("MineStrator Server ID is not configured properly!");
            return CompletableFuture.completedFuture(false);
        }

        String jsonBody = "{\"command\":\"" + command + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + serverId + "/command"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
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
        return sendCommand("stop");
    }

    public CompletableFuture<Boolean> restartServer() {
        return sendCommand("restart");
    }
}
