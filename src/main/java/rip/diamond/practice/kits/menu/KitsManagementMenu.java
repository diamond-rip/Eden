package rip.diamond.practice.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

public class KitsManagementMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.KIT_KIT_MANAGEMENT_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().forEach(kit -> {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kit.getDisplayIcon().clone())
                            .name(Language.KIT_KIT_MANAGEMENT_MENU_BUTTON_NAME.toString(kit.getDisplayName(), kit.getName()))
                            .lore(Language.KIT_KIT_MANAGEMENT_MENU_BUTTON_LORE.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                    new KitDetailsMenu(kit, KitsManagementMenu.this).openMenu(player);
                }
            });
        });

        return buttons;
    }
}
