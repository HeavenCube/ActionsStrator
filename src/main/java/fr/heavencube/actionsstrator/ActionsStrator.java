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
    }

    @Override
    public void onDisable() {
    }

    public MineStratorClient getApiClient() {
        return apiClient;
    }
}
