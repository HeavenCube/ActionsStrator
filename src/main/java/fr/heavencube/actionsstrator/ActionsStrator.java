package fr.heavencube.actionsstrator;

import org.bukkit.plugin.java.JavaPlugin;
import fr.heavencube.actionsstrator.api.MineStratorClient;

public class ActionsStrator extends JavaPlugin {

    private MineStratorClient apiClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String apiKey = getConfig().getString("api_key");
        String serverId = getConfig().getString("server_id");

        apiClient = new MineStratorClient(apiKey, serverId, getLogger());

        getServer().getCommandMap().register("actionsstrator",new fr.heavencube.actionsstrator.commands.RestartCommand(apiClient));
        getServer().getCommandMap().register("actionsstrator",new fr.heavencube.actionsstrator.commands.StopCommand(apiClient));
        getServer().getCommandMap().register("actionsstrator",new fr.heavencube.actionsstrator.commands.KillCommand(apiClient));

        // Fetch and display server information
        apiClient.getServerInfo().thenAccept(serverInfo -> {
            if (serverInfo != null) {
                displayServerInfo(serverInfo);
            } else {
                getLogger().warning("Could not fetch server information from MineStrator API");
            }
        });
    }

    @Override
    public void onDisable() {
    }

    public MineStratorClient getApiClient() {
        return apiClient;
    }

    private void displayServerInfo(MineStratorClient.ServerInfo serverInfo) {
        getLogger().info("╔════════════════════════════════════════════╗");
        getLogger().info("║        ActionsStrator v" + getPluginMeta().getVersion() + " - Plugin Enabled        ║");
        getLogger().info("╠════════════════════════════════════════════╣");
        getLogger().info("║  🖥️  Server      : " + padRight(serverInfo.serverName, 28) + " ║");
        getLogger().info("║  📦 Offer       : " + padRight(serverInfo.offerName, 29) + " ║");
        getLogger().info("║  ☁️  MyBox       : " + padRight(serverInfo.myboxName, 29) + " ║");
        getLogger().info("╠════════════════════════════════════════════╣");
        getLogger().info("║  ✓ Plugin successfully connected to API!   ║");
        getLogger().info("╚════════════════════════════════════════════╝");
    }

    private String padRight(String str, int length) {
        if (str.length() >= length) {
            return str.substring(0, Math.min(str.length(), length));
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
