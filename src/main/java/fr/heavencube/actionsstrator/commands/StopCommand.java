package fr.heavencube.actionsstrator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.utils.Messages;

public class StopCommand extends Command {

    private final MineStratorClient client;

    public StopCommand(MineStratorClient client) {
        super("msstop", "Stop the server via MineStrator API", "/msstop", List.of());
        this.client = client;
        setPermission("actionsstrator.stop");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.stop")) { Messages.send(sender, Messages.NO_PERMISSION);return true;}

        Messages.send(sender, Messages.STOP_SENDING);
        client.sendPowerAction("stop").thenAccept(success -> {
            if (success) Messages.send(sender, Messages.STOP_SUCCESS);
            else Messages.send(sender, Messages.STOP_FAILED);
        });
        return true;
    }
}