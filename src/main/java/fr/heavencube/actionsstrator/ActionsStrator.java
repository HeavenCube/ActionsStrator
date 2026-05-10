package fr.heavencube.actionsstrator;

import org.bukkit.plugin.java.JavaPlugin;
import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.commands.CommandRegistry;

public class ActionsStrator extends JavaPlugin {
    private MineStratorClient apiClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String apiKey = getConfig().getString("api_key");
        String serverId = getConfig().getString("server_id");

        // Initialize API client
        apiClient = new MineStratorClient(apiKey, serverId, getLogger());

        // Register all commands
        CommandRegistry.registerAll(getServer(), apiClient);

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
        // Cleanup if needed
    }

    private void displayServerInfo(MineStratorClient.ServerInfo serverInfo) {
        String line = "─".repeat(55);
        
        getLogger().info("");
        getLogger().info("╭" + line + "╮");
        getLogger().info("│ ActionsStrator v" + getPluginMeta().getVersion());
        getLogger().info("├" + line + "┤");
        getLogger().info("│ 🖥️  MyBox  → " + serverInfo.myboxName);
        getLogger().info("│ 📦 Offer  → " + serverInfo.offerName);
        getLogger().info("│ 🔎  Server → " + serverInfo.serverName);
        getLogger().info("├" + line + "┤");
        getLogger().info("│ ✓ Connected to MineStrator API successfully");
        getLogger().info("╰" + line + "╯");
        getLogger().info("");
    }
}
