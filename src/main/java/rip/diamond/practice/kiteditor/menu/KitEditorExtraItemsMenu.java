package rip.diamond.practice.kiteditor.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitExtraItem;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitEditorExtraItemsMenu extends PaginatedMenu {
    private final Kit kit;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.KIT_EDITOR_EXTRA_ITEM_MENU_NAME.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (KitExtraItem item : kit.getKitExtraItems()) {
            ItemBuilder builder = new ItemBuilder(item.getMaterial()).amount(item.getAmount()).durability(item.getData()).enchantments(item.getEnchantments());
            if (item.getName() != null) {
                builder.name(item.getName());
            }
            if (item.isUnbreakable()) {
                builder.unbreakable();
            }

            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return builder.build();
                }
                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.setItemOnCursor(builder.build());
                }
            });
        }
        return buttons;
    }
}
