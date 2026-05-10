package fr.heavencube.actionsstrator.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import fr.heavencube.actionsstrator.api.MineStratorClient;

public class MineStratorCommands implements CommandExecutor {

    private final MineStratorClient client;

    public MineStratorCommands(MineStratorClient client) {
        this.client = client;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("msrestart")) {
            if (!sender.hasPermission("actionsstrator.restart")) {
                sender.sendMessage(
                        Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                return true;
            }

            sender.sendMessage(Component.text("Sending restart signal to MineStrator...", NamedTextColor.YELLOW));
            client.restartServer().thenAccept(success -> {
                if (success) {
                    sender.sendMessage(Component.text("Restart signal successfully sent!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Failed to send restart signal. Check console for details.",
                            NamedTextColor.RED));
                }
            });
            return true;

        } else if (command.getName().equalsIgnoreCase("msstop")) {
            if (!sender.hasPermission("actionsstrator.stop")) {
                sender.sendMessage(
                        Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                return true;
            }

            sender.sendMessage(Component.text("Sending stop signal to MineStrator...", NamedTextColor.YELLOW));
            client.stopServer().thenAccept(success -> {
                if (success) {
                    sender.sendMessage(Component.text("Stop signal successfully sent!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Failed to send stop signal. Check console for details.",
                            NamedTextColor.RED));
                }
            });
            return true;
        }

        return false;
    }
}
