package rip.diamond.practice.util.menu;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Button {

	public static Button placeholder(final Material material, final byte data, String... title) {
		return (new Button() {
			public ItemStack getButtonItem(Player player) {
				ItemStack it = new ItemStack(material, 1, data);
				ItemMeta meta = it.getItemMeta();

				meta.setDisplayName(StringUtils.join(title));
				it.setItemMeta(meta);

				return it;
			}
		});
	}

	public static void playFail(Player player) {
		player.playSound(player.getLocation(), Sound.DIG_GRASS, 20F, 0.1F);

	}

	public static void playSuccess(Player player) {
		player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20F, 15F);
	}

	public static void playNeutral(Player player) {
		player.playSound(player.getLocation(), Sound.CLICK, 20F, 1F);
	}

	public abstract ItemStack getButtonItem(Player player);

	public void clicked(Player player, ClickType clickType) {}

	public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {}

	public boolean shouldCancel(Player player, ClickType clickType) {
		return true;
	}

	public boolean shouldUpdate(Player player, ClickType clickType) {
		return false;
	}

}