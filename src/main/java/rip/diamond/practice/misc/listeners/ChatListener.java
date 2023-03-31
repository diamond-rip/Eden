package rip.diamond.practice.misc.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.util.Checker;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final Eden plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (Config.CHAT_FORMAT_ENABLED.toBoolean()) {
            String format = Config.CHAT_FORMAT_FORMAT.toString();
            if (Checker.isPluginEnabled("PlaceholderAPI")) {
                format = plugin.getHookManager().getPlaceholderAPIHook().setPlaceholders(player, format);
            }
            event.setFormat(format);
        }
    }

}
