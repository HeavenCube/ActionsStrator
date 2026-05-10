package fr.heavencube.actionsstrator.utils;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.commands.RestartCommand;
import fr.heavencube.actionsstrator.commands.Restart10SecCommand;
import fr.heavencube.actionsstrator.commands.StopCommand;
import fr.heavencube.actionsstrator.commands.Stop10SecCommand;
import fr.heavencube.actionsstrator.commands.KillCommand;

import org.bukkit.Server;

public class CommandRegistry {
    
    public static void registerAll(Server server, MineStratorClient client) {
        RestartCommand.register(server, client);
        Restart10SecCommand.register(server, client);
        StopCommand.register(server, client);
        Stop10SecCommand.register(server, client);
        KillCommand.register(server, client);
    }
}
