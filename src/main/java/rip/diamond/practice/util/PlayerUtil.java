package rip.diamond.practice.util;

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {

	public static void reset(Player player) {
		reset(player, true);
	}

	public static void reset(Player player, boolean resetHeldSlot) {
		if (player == null) {
			return;
		}
		player.setHealth(20.0D);
		player.setSaturation(0.0F);
		player.setFallDistance(0.0F);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.setItemOnCursor(null);
		player.getOpenInventory().getTopInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
		player.spigot().setCollidesWithEntities(true);

		clearArrow(player);

		//Since Player#sendTitle only reset the title, not subtitle, so we do some tricky stuff here
		TitleSender.sendTitle(player, "&r", PacketPlayOutTitle.EnumTitleAction.TITLE, 1, 10, 1);
		TitleSender.sendTitle(player, "&r", PacketPlayOutTitle.EnumTitleAction.SUBTITLE, 1, 10, 1);

		if (resetHeldSlot) {
			player.getInventory().setHeldItemSlot(0);
		}

		player.updateInventory();
	}

	public static void spectator(Player player) {
		reset(player);

		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);

		player.setAllowFlight(true);
		player.setFlying(true);
		player.spigot().setCollidesWithEntities(false);

		clearArrow(player);
	}

	public static void clearArrow(Player player) {
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
	}
}