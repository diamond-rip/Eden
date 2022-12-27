package rip.diamond.practice.hook.spigot.impl;

import dev.imanity.knockback.api.Knockback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.imanity.imanityspigot.chunk.AsyncPriority;
import rip.diamond.practice.Language;
import rip.diamond.practice.hook.spigot.SpigotController;
import rip.diamond.practice.hook.spigot.SpigotType;

public class ImanitySpigot3 extends SpigotController {

    @Override
    public SpigotType getSpigotType() {
        return SpigotType.IMANITY_SPIGOT_3;
    }

    @Override
    public void applyKnockback(Player player, String knockbackName) {
        Knockback knockback = Bukkit.imanity().getKnockbackService().getKnockbackByName(knockbackName);
        if (knockback == null) {
            Language.HOOK_ERROR_KNOCKBACK_NOT_FOUND.sendMessage(player);
            return;
        }
        Bukkit.imanity().getKnockbackService().setKnockback(player, knockback);
    }

    public void teleportAsync(Player player, Location location) {
        location.getWorld().imanity().getChunkAtAsynchronously(location, AsyncPriority.HIGHER).thenApply(chunk -> player.teleport(location));
    }
}
