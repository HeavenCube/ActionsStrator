package fr.heavencube.actionsstrator.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import fr.heavencube.actionsstrator.api.MineStratorClient;

public class StopCommand extends Command {

    private final MineStratorClient client;

    public StopCommand(MineStratorClient client) {
        super("msstop", "Stop the server via MineStrator API", "/msstop", List.of());
        setPermission("actionsstrator.stop");
        this.client = client;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.stop")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("Sending stop signal to MineStrator...", NamedTextColor.YELLOW));
        client.stopServer().thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("Stop signal successfully sent!", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(
                        Component.text("Failed to send stop signal. Check console for details.", NamedTextColor.RED));
            }
        });
        return true;
    }
}
