package rip.diamond.practice.hook.spigot.impl;

import org.bukkit.entity.Player;
import rip.diamond.practice.hook.spigot.SpigotController;
import rip.diamond.practice.hook.spigot.SpigotType;

public class DefaultSpigot extends SpigotController {

    @Override
    public SpigotType getSpigotType() {
        return SpigotType.SPIGOT;
    }

    @Override
    public void applyKnockback(Player player, String knockbackName) {

    }
}
