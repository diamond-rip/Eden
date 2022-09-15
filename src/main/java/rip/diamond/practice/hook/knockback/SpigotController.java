package rip.diamond.practice.hook.knockback;

import org.bukkit.entity.Player;

public abstract class SpigotController {


    public abstract String getPluginName();

    public abstract String getPackage();

    public abstract void applyKnockback(Player player, String knockbackName);

}
