package rip.diamond.practice.hook.spigot;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.imanity.imanityspigot.chunk.AsyncPriority;

public class ImanitySpigot3Hook {

    public void teleportAsync(Player player, Location location) {
        location.getWorld().imanity().getChunkAtAsynchronously(location, AsyncPriority.HIGHER).thenApply(chunk -> player.teleport(location));
    }

}
