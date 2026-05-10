package fr.heavencube.actionsstrator.commands;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.utils.Messages;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Restart10SecCommand extends Command {

    private final MineStratorClient client;

    public static void register(Server server, MineStratorClient client) {
        server.getCommandMap().register("actionsstrator", new Restart10SecCommand(client));
    }

    public Restart10SecCommand(MineStratorClient client) {
        super("msrestart10sec", "Restart the server with 10 second delay via MineStrator API", "/msrestart10sec", List.of());
        this.client = client;
        setPermission("actionsstrator.restart");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.restart")) {
            Messages.send(sender, Messages.NO_PERMISSION); 
            return true;
        }

        if (!client.isActionConfigured("restart10")) {
            Messages.send(sender, Messages.ACTION_NOT_CONFIGURED);
            return true;
        }

        Messages.send(sender, Messages.RESTART_SENDING);
        client.sendPowerAction("restart10").thenAccept(success -> {
            if (success) Messages.send(sender, Messages.RESTART_SUCCESS);
            else Messages.send(sender, Messages.RESTART_FAILED);
        });
        return true;
    }
}