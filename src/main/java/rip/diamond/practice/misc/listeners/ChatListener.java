package rip.diamond.practice.misc.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rip.diamond.practice.Eden;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (Eden.INSTANCE.getConfigFile().getBoolean("chat-format.enabled")) {
            String format = PlaceholderAPI.setPlaceholders(player, Eden.INSTANCE.getConfigFile().getString("chat-format.format"));
            event.setFormat(format);
        }
    }

}
