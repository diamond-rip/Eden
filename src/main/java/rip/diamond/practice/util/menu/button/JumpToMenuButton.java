package rip.diamond.practice.util.menu.button;

import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class JumpToMenuButton extends Button {

	private Menu menu;
	private ItemStack itemStack;

	public JumpToMenuButton(Menu menu, ItemStack itemStack) {
		this.menu = menu;
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		return itemStack;
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		menu.openMenu(player);
	}

}
