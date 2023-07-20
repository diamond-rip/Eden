package rip.diamond.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;

import java.util.List;

public class Common {

    public static void log(String... str) {
        for (String string : str) {
            Bukkit.getConsoleSender().sendMessage(CC.RED + "[LOG] " + CC.translate(string));
        }
    }

    public static void debug(String... str) {
        for (String string : str) {
             debug(string);
        }
    }

    public static void debug(String str) {
        if (Config.DEBUG.toBoolean()) {
            Bukkit.getConsoleSender().sendMessage(CC.RED + "[除錯] " + CC.translate(str));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("eden.debug")) {
                    sendMessage(player, CC.DARK_GRAY + "[除錯] " + str);
                }
            }
        }
    }

    public static void broadcastMessage(String... str) {
        for (String string : str) {
            Bukkit.broadcastMessage(CC.translate(string));
        }
    }

    public static void broadcastMessage(List<String> str) {
        for (String string : str) {
            Bukkit.broadcastMessage(CC.translate(string));
        }
    }

    public static void broadcastSound(Sound sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, 10, 1);
        }
    }

    public static void broadcastSound(Sound sound, float v, float v1) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, v, v1);
        }
    }

    public static void sendMessage(CommandSender sender, String... str) {
        if (sender == null) {
            return;
        }
        for (String s : str) {
            sender.sendMessage(CC.translate(s));
        }
    }

    public static void sendMessage(Player player, String... str) {
        if (player == null) {
            return;
        }
        for (String s : str) {
            if (Util.isNull(s)) {
                continue;
            }
            player.sendMessage(CC.translate(s));
        }
    }

    public static void sendMessage(Player player, List<String> str) {
        if (player == null) {
            return;
        }
        for (String s : str) {
            if (Util.isNull(s)) {
                continue;
            }
            player.sendMessage(CC.translate(s));
        }
    }

    public static void playSound(Player player, Sound sound) {
        if (player == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public static void playSound(Player player, Sound sound, float v, float v1) {
        if (player == null) {
            return;
        }
        player.playSound(player.getLocation(), sound, v, v1);
    }

    public static boolean hasPermission(CommandSender sender, List<String> permissions) {
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) return true;
        }
        return false;
    }

}
