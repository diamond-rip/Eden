package rip.diamond.practice.misc.listeners;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rip.diamond.practice.Eden;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final Eden plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfigFile().getBoolean("chat-format.enabled")) {
            String format = plugin.getConfigFile().getString("chat-format.format");
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                format = PlaceholderAPI.setPlaceholders(player, format);
            }
            event.setFormat(format);
        }
    }

}
