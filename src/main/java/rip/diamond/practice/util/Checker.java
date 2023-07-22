package rip.diamond.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.queue.QueueType;

import java.util.UUID;

public class Checker {

    public static boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean isInteger(String index) {
        try {
            Integer.parseInt(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isDouble(String index) {
        try {
            Double.parseDouble(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isFloat(String index) {
        try {
            Float.parseFloat(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isBoolean(String index) {
        return index.equals("true") || index.equals("false");
    }

    public static boolean isUUID(String index) {
        try {
            UUID.fromString(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isMaterial(String index) {
        try {
            Material.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isEnchantment(String index) {
        try {
            Enchantment.getByName(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isPotionEffect(String index) {
        try {
            PotionEffectType.getByName(index);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isItemFlag(String index) {
        try {
            ItemFlag.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isQueueType(String index) {
        try {
            QueueType.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isEventType(String index) {
        try {
            EventType.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isKitMatchType(String index) {
        try {
            KitMatchType.valueOf(index.toUpperCase());
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isClassExists(String string) {
        try {
            Class.forName(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canDamage(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);
        if (Util.isNPC(player)) {
            return profile != null;
        }
        return profile.getPlayerState() == PlayerState.IN_MATCH
                && profile.getMatch() != null
                && profile.getMatch().getTeamPlayer(player).isAlive()
                && !profile.getMatch().getTeamPlayer(player).isRespawning()
                && profile.getMatch().getState() == MatchState.FIGHTING;
    }

}
