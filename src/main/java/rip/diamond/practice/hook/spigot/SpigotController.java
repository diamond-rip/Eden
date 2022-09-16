package rip.diamond.practice.hook.spigot;

import org.bukkit.entity.Player;

public abstract class SpigotController {

    public abstract SpigotType getSpigotType();

    public abstract void applyKnockback(Player player, String knockbackName);

}
