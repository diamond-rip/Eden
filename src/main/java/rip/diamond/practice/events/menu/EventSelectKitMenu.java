package rip.diamond.practice.events.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EventSelectKitMenu extends Menu {
    private final EventSettingsMenu backMenu;

    @Override
    public String getTitle(Player player) {
        return Language.EVENT_EVENT_SELECT_KIT_MENU_TITLE.toString(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().stream()
                .filter(Kit::isEnabled)
                .filter(kit -> kit.getKitMatchTypes().contains(KitMatchType.SOLO) && kit.getKitMatchTypes().contains(KitMatchType.SPLIT))
                .forEach(kit -> buttons.put(buttons.size(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(kit.getDisplayIcon().clone())
                                .name(Language.EVENT_EVENT_SELECT_KIT_MENU_BUTTON_NAME.toString(player, kit.getDisplayName()))
                                .lore(Language.EVENT_EVENT_SELECT_KIT_MENU_BUTTON_LORE.toStringList(player))
                                .build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        player.closeInventory();
                        backMenu.setKit(kit);
                        backMenu.openMenu(player);
                    }
                }));

        return buttons;
    }
}
