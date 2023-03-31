package rip.diamond.practice.kiteditor.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class KitEditorSelectKitMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Language.KIT_EDITOR_SELECT_KIT_MENU_NAME.toString();
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		Kit.getKits().stream().filter(kit -> kit.getGameRules().isReceiveKitLoadoutBook()).forEach(kit -> {
			if (kit.isEnabled()) {
				buttons.put(buttons.size(), new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return new ItemBuilder(kit.getDisplayIcon().clone())
								.name(Language.KIT_EDITOR_SELECT_KIT_MENU_BUTTON_NAME.toString(kit.getDisplayName()))
								.lore(Language.KIT_EDITOR_SELECT_KIT_MENU_BUTTON_LORE.toStringList(player))
								.build();
					}

					@Override
					public void clicked(Player player, ClickType clickType) {
						player.closeInventory();
						Eden.INSTANCE.getKitEditorManager().addKitEditor(player, kit);
					}
				});
			}
		});

		return buttons;
	}
}
