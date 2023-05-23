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
		player.setSaturation(20.0F);
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

		TitleSender.sendTitle(player, "", PacketPlayOutTitle.EnumTitleAction.TITLE, 0, 1, 0);
		TitleSender.sendTitle(player, "", PacketPlayOutTitle.EnumTitleAction.SUBTITLE, 0, 1, 0);

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
		player.setFlySpeed(0.1F);
		player.updateInventory();
		player.spigot().setCollidesWithEntities(false);

		//Try to double set flying state and see if it fixes the multiworld problem - TODO
		player.setAllowFlight(true);
		player.setFlying(true);

		clearArrow(player);
	}

	public static void clearArrow(Player player) {
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
	}
}