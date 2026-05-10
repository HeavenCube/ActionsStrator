package fr.heavencube.actionsstrator.commands;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import org.bukkit.Server;

public class CommandRegistry {
    
    public static void registerAll(Server server, MineStratorClient client) {
        RestartCommand.register(server, client);
        StopCommand.register(server, client);
        KillCommand.register(server, client);
    }
}
