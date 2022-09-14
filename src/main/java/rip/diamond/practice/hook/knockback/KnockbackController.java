package rip.diamond.practice.hook.knockback;

import org.bukkit.entity.Player;
import rip.diamond.practice.hook.Hook;

public abstract class KnockbackController implements Hook {

    public abstract void applyKnockback(Player player, String knockbackName);

}
