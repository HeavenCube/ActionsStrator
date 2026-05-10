package fr.heavencube.actionsstrator;

import org.bukkit.plugin.java.JavaPlugin;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.commands.MineStratorCommands;

public class ActionsStrator extends JavaPlugin {

    private MineStratorClient apiClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String apiKey = getConfig().getString("api_key");
        String serverId = getConfig().getString("server_id");
        String userId = getConfig().getString("user_id");
        String myboxId = getConfig().getString("mybox_id");

        apiClient = new MineStratorClient(apiKey, serverId, userId, myboxId, getLogger());
        
        // Connect to the WebSocket console
        apiClient.connectToConsole();

        MineStratorCommands commandExecutor = new MineStratorCommands(apiClient);

        org.bukkit.command.Command msrestart = new org.bukkit.command.Command("msrestart") {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return commandExecutor.onCommand(sender, this, commandLabel, args);
            }
        };
        msrestart.setDescription("Restart the server via MineStrator API");
        msrestart.setPermission("actionsstrator.restart");

        org.bukkit.command.Command msstop = new org.bukkit.command.Command("msstop") {
            @Override
            public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
                return commandExecutor.onCommand(sender, this, commandLabel, args);
            }
        };
        msstop.setDescription("Stop the server via MineStrator API");
        msstop.setPermission("actionsstrator.stop");

        getServer().getCommandMap().register("actionsstrator", msrestart);
        getServer().getCommandMap().register("actionsstrator", msstop);

        getLogger().info("ActionsStrator has been enabled!");
    }

    @Override
    public void onDisable() {
        if (apiClient != null) {
            apiClient.disconnectConsole();
        }
        getLogger().info("ActionsStrator has been disabled!");
    }

    public MineStratorClient getApiClient() {
        return apiClient;
    }
}
