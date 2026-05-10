package fr.heavencube.actionsstrator.commands;

import fr.heavencube.actionsstrator.api.MineStratorClient;
import fr.heavencube.actionsstrator.utils.Messages;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KillCommand extends Command {

    private final MineStratorClient client;

    public static void register(Server server, MineStratorClient client) {
        server.getCommandMap().register("actionsstrator", new KillCommand(client));
    }

    public KillCommand(MineStratorClient client) {
        super("mskill", "Kill the server via MineStrator API", "/mskill", List.of());
        this.client = client;
        setPermission("actionsstrator.kill");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.kill")) {
            Messages.send(sender, Messages.NO_PERMISSION); 
            return true;
        }

        Messages.send(sender, Messages.KILL_SENDING);
        client.sendPowerAction("kill").thenAccept(success -> {
            if (success) Messages.send(sender, Messages.KILL_SUCCESS);
            else Messages.send(sender, Messages.KILL_FAILED);
        });
        return true;
    }
}