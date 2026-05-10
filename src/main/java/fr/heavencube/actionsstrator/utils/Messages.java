package fr.heavencube.actionsstrator.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

public class Messages {
    // COMMON MESSAGES
    public static final String PREFIX = "<c:#009B72><b>MɪɴᴇSᴛʀᴀᴛᴏʀ</b></c> <dark_gray>|</dark_gray> ";
    public static final String NO_PERMISSION = PREFIX + "<c:#F95C5E>You do not have permission to use this command.</c>";
    // RESTART MESSAGES
    public static final String RESTART_SENDING = PREFIX + "<c:#EAC435>Sending restart signal to MineStrator...</c>";
    public static final String RESTART_SUCCESS = PREFIX + "<c:#009B72>Restart signal successfully sent!</c>";
    public static final String RESTART_FAILED = PREFIX + "<c:#F95C5E>Failed to send restart signal. Check console for details.</c>";
    // STOP MESSAGES
    public static final String STOP_SENDING = PREFIX + "<c:#EAC435>Sending stop signal to MineStrator...</c>";
    public static final String STOP_SUCCESS = PREFIX + "<c:#009B72>Stop signal successfully sent!</c>";
    public static final String STOP_FAILED = PREFIX + "<c:#F95C5E>Failed to send stop signal. Check console for details.</c>";
    // KILL MESSAGES
    public static final String KILL_SENDING = PREFIX + "<c:#EAC435>Sending kill signal to MineStrator...</c>";
    public static final String KILL_SUCCESS = PREFIX + "<c:#009B72>Kill signal successfully sent!</c>";
    public static final String KILL_FAILED = PREFIX + "<c:#F95C5E>Failed to send kill signal. Check console for details.</c>";
    
    
    /**
     * Send a parsed MiniMessage string to the sender.
     */
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }
}
