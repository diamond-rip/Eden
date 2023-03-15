package rip.diamond.practice.util.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.diamond.practice.Eden;
import rip.diamond.practice.event.MenuOpenEvent;
import rip.diamond.practice.event.MenuUpdateEvent;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.menu.task.MenuUpdateTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class Menu {

	public static Map<UUID, Menu> currentlyOpenedMenus = new ConcurrentHashMap<>();

	@Getter protected Eden plugin = Eden.INSTANCE;
	private Map<Integer, Button> buttons = new ConcurrentHashMap<>();
	private boolean autoUpdate = false;
	private boolean closedByMenu = false;
	private boolean placeholder = false;
	public Button placeholderButton = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " ");

	public static void init() {
		new MenuUpdateTask();
	}

	private ItemStack createItemStack(Player player, Button button) {
		ItemStack item = button.getButtonItem(player);

		if (item.getType() != Material.SKULL_ITEM) {
			ItemMeta meta = item.getItemMeta();

			if (meta != null && meta.hasDisplayName()) {
				meta.setDisplayName(meta.getDisplayName());
			}

			item.setItemMeta(meta);
		}

		return item;
	}

	public void openMenu(final Player player) {
		try {
			//Eden Start - Recoded how menu opens. Might contain bugs
			Menu previousMenu = Menu.currentlyOpenedMenus.get(player.getUniqueId());
			if (previousMenu != null) {
				previousMenu.onClose(player);
				previousMenu.setClosedByMenu(true);
				Menu.currentlyOpenedMenus.remove(player.getUniqueId());

				MenuUpdateEvent event = new MenuUpdateEvent(this);
				event.call();
			} else {
				MenuOpenEvent event = new MenuOpenEvent(this);
				event.call();
			}

			this.buttons = this.getButtons(player);
			String title = CC.translate(CC.AQUA + this.getTitle(player));
			if (title.length() > 32) {
				title = title.substring(0, 32);
			}
			int size = this.getSize() == -1 ? this.size(this.buttons) : this.getSize();

			Inventory inventory = Bukkit.createInventory(player, size, title);

			for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
				inventory.setItem(buttonEntry.getKey(), createItemStack(player, buttonEntry.getValue()));
			}

			if (this.isPlaceholder()) {
				for (int index = 0; index < size; index++) {
					if (this.buttons.get(index) == null) {
						this.buttons.put(index, this.placeholderButton);
						inventory.setItem(index, this.placeholderButton.getButtonItem(player));
					}
				}
			}

			player.openInventory(inventory);
			Menu.currentlyOpenedMenus.put(player.getUniqueId(), this);
			this.onOpen(player);
			this.setClosedByMenu(false);
			//Eden End
		} catch (Exception e) {
			e.printStackTrace();
			player.closeInventory();
		}
	}

	public int size(Map<Integer, Button> buttons) {
		int highest = 0;

		for (int buttonValue : buttons.keySet()) {
			if (buttonValue > highest) {
				highest = buttonValue;
			}
		}

		return (int) (Math.ceil((highest + 1) / 9D) * 9D);
	}

	public int getSize() {
		return -1;
	}

	public int getSlot(int x, int y) {
		return ((9 * y) + x);
	}

	public abstract String getTitle(Player player);

	public String getID() {
		return getClass().getSimpleName();
	}

	public abstract Map<Integer, Button> getButtons(Player player);

	public void onOpen(Player player) {
	}

	public void onClose(Player player) {
	}

}
