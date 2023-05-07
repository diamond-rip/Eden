package rip.diamond.practice.util;

import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageCalculator {

    public static double getDamage(ItemStack weapon) {
        double damage;
        switch(weapon.getType()) {
            case STONE_SPADE:
            case WOOD_PICKAXE:
            case GOLD_PICKAXE:
                damage = 2;
                break;
            case IRON_SPADE:
            case WOOD_AXE:
            case GOLD_AXE:
            case STONE_PICKAXE:
                damage = 3;
                break;
            case WOOD_SWORD:
            case GOLD_SWORD:
            case STONE_AXE:
            case IRON_PICKAXE:
            case DIAMOND_SPADE:
                damage = 4;
                break;
            case STONE_SWORD:
            case IRON_AXE:
            case DIAMOND_PICKAXE:
                damage = 5;
                break;
            case IRON_SWORD:
            case DIAMOND_AXE:
                damage = 6;
                break;
            case DIAMOND_SWORD:
                damage = 7;
                break;
            default:
                damage = 0;
                break;
        }
        return damage;
    }

    //Credit: Sentinel (https://github.com/mcmonkeyprojects/Sentinel/blob/master/src/main/java/org/mcmonkey/sentinel/SentinelTrait.java)
    public static double getEnchantedDamage(ItemStack weapon) {
        double enchantBoost = weapon.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 1.25;
        return getDamage(weapon) + enchantBoost;
    }

}
