package rip.diamond.practice.hook.knockback.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.imanity.imanityspigot.knockback.Knockback;
import rip.diamond.practice.Language;
import rip.diamond.practice.hook.knockback.KnockbackController;

public class ImanitySpigot3Knockback extends KnockbackController {

    @Override
    public void applyKnockback(Player player, String knockbackName) {
        Knockback knockback = Bukkit.imanity().getKnockbackService().getKnockbackByName(knockbackName);
        if (knockback == null) {
            Language.HOOK_ERROR_KNOCKBACK_NOT_FOUND.sendMessage(player);
            return;
        }
        Bukkit.imanity().setKnockback(player, knockback);
    }

    @Override
    public String getPluginName() {
        return "ImanitySpigot3";
    }

    @Override
    public String getPackage() {
        return "org.imanity.imanityspigot.ImanitySpigot";
    }
}
