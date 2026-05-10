package fr.heavencube.actionsstrator.commands;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.utils.Messages;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Stop10SecCommand extends Command {

    private final MineStratorClient client;

    public static void register(Server server, MineStratorClient client) {
        server.getCommandMap().register("actionsstrator", new Stop10SecCommand(client));
    }

    public Stop10SecCommand(MineStratorClient client) {
        super("msstop10sec", "Stop the server with 10 second delay via MineStrator API", "/msstop10sec", List.of());
        this.client = client;
        setPermission("actionsstrator.stop");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.stop")) {
            Messages.send(sender, Messages.NO_PERMISSION); 
            return true;
        }

        Messages.send(sender, Messages.STOP_SENDING);
        client.sendPowerAction("stop10").thenAccept(success -> {
            if (success) Messages.send(sender, Messages.STOP_SUCCESS);
            else Messages.send(sender, Messages.STOP_FAILED);
        });
        return true;
    }
}