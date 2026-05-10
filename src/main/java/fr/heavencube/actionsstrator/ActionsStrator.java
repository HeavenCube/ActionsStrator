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

        apiClient = new MineStratorClient(apiKey, serverId, getLogger());

        MineStratorCommands commandExecutor = new MineStratorCommands(apiClient);

        if (getCommand("msrestart") != null) {
            getCommand("msrestart").setExecutor(commandExecutor);
        }
        if (getCommand("msstop") != null) {
            getCommand("msstop").setExecutor(commandExecutor);
        }

        getLogger().info("ActionsStrator has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ActionsStrator has been disabled!");
    }

    public MineStratorClient getApiClient() {
        return apiClient;
    }
}
