package rip.diamond.practice.util.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import rip.diamond.practice.Eden;

public class MenuListener implements Listener {

	private final Eden plugin;

	public MenuListener(Eden plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onButtonPress(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();
		Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

		if (openMenu != null) {
			if (event.getSlot() != event.getRawSlot()) {
				if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
					event.setCancelled(true);
				}

				return;
			}

			if (openMenu.getButtons().containsKey(event.getSlot())) {
				Button button = openMenu.getButtons().get(event.getSlot());
				boolean cancel = button.shouldCancel(player, event.getClick());

				if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
					event.setCancelled(true);

					if (event.getCurrentItem() != null) {
						player.getInventory().addItem(event.getCurrentItem());
					}
				} else {
					event.setCancelled(cancel);
				}

				button.clicked(player, event.getClick());
				button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());

				if (Menu.currentlyOpenedMenus.containsKey(player.getUniqueId())) {
					Menu newMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

					if (newMenu == openMenu) {
						boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

						if (buttonUpdate) {
							openMenu.setClosedByMenu(true);
							newMenu.openMenu(player);
						}
					}
				} else if (button.shouldUpdate(player, event.getClick())) {
					openMenu.setClosedByMenu(true);
					openMenu.openMenu(player);
				}

				if (event.isCancelled()) {
					Bukkit.getScheduler().runTaskLater(plugin, player::updateInventory, 1L);
				}
			} else {
				if (event.getCurrentItem() != null) {
					event.setCancelled(true);
				}

				if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Menu openMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());

		if (openMenu != null) {
			openMenu.onClose(player);

			Menu.currentlyOpenedMenus.remove(player.getUniqueId());
		}
	}

}