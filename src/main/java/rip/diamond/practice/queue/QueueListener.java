package rip.diamond.practice.queue;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!Queue.getPlayers().containsKey(player.getUniqueId())) {
            return;
        }

        Queue.leaveQueue(player);
    }

}
