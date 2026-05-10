package fr.heavencube.actionsstrator.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.utils.Messages;

public class RestartCommand extends Command {

    private final MineStratorClient client;

    public RestartCommand(MineStratorClient client) {
        super("msrestart", "Restart the server via MineStrator API", "/msrestart", List.of());
        this.client = client;
        setPermission("actionsstrator.restart");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.restart")) {
            Messages.send(sender, Messages.NO_PERMISSION);
            return true;
        }

        Messages.send(sender, Messages.RESTART_SENDING);
        client.restartServer().thenAccept(success -> {
            if (success) {
                Messages.send(sender, Messages.RESTART_SUCCESS);
            } else {
                Messages.send(sender, Messages.RESTART_FAILED);
            }
        });
        return true;
    }
}
