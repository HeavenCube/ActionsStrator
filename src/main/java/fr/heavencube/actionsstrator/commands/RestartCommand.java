package fr.heavencube.actionsstrator.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import fr.heavencube.actionsstrator.api.MineStratorClient;

public class RestartCommand extends Command {

    private final MineStratorClient client;

    public RestartCommand(MineStratorClient client) {
        super("msrestart", "Restart the server via MineStrator API", "/msrestart", List.of());
        setPermission("actionsstrator.restart");
        this.client = client;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("actionsstrator.restart")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("Sending restart signal to MineStrator...", NamedTextColor.YELLOW));
        client.restartServer().thenAccept(success -> {
            if (success) {
                sender.sendMessage(Component.text("Restart signal successfully sent!", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("Failed to send restart signal. Check console for details.", NamedTextColor.RED));
            }
        });
        return true;
    }
}
