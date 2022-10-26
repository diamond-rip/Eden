package rip.diamond.practice.hook.spigot.impl;

import org.bukkit.entity.Player;
import rip.diamond.practice.Language;
import rip.diamond.practice.hook.spigot.SpigotController;
import rip.diamond.practice.hook.spigot.SpigotType;
import xyz.refinedev.spigot.api.knockback.KnockbackAPI;
import xyz.refinedev.spigot.knockback.KnockbackProfile;

public class CarbonSpigot extends SpigotController {

    @Override
    public SpigotType getSpigotType() {
        return SpigotType.CARBON_SPIGOT;
    }

    @Override
    public void applyKnockback(Player player, String knockbackName) {
        KnockbackProfile knockback = KnockbackAPI.getInstance().getProfile(knockbackName);
        if (knockback == null) {
            Language.HOOK_ERROR_KNOCKBACK_NOT_FOUND.sendMessage(player);
            return;
        }
        KnockbackAPI.getInstance().setPlayerProfile(player, knockback);
    }
}
