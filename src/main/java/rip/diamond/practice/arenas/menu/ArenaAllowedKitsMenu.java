package rip.diamond.practice.arenas.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;
import rip.diamond.practice.util.menu.button.BackButton;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ArenaAllowedKitsMenu extends PaginatedMenu {

    private final Arena arena;
    private final Menu backMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.ARENA_ALLOWED_KITS_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : Kit.getKits()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kit.getDisplayIcon())
                            .name(kit.getDisplayName())
                            .lore("", arena.getAllowedKits().contains(kit.getName()) ? Language.ARENA_ALLOWED_KITS_MENU_LORE_SELECTED.toString() : Language.ARENA_ALLOWED_KITS_MENU_LORE_CLICK_TO_SELECT.toString())
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (arena.getAllowedKits().contains(kit.getName())) {
                        arena.getAllowedKits().remove(kit.getName());
                        Language.ARENA_EDIT_ALLOWED_KITS_REMOVED.sendMessage(player, kit.getName(), arena.getName());
                    } else {
                        arena.getAllowedKits().add(kit.getName());
                        Language.ARENA_EDIT_ALLOWED_KITS_ADDED.sendMessage(player, kit.getName(), arena.getName());
                    }
                    arena.setEdited(true);
                    arena.autoSave();

                    openMenu(player);
                }
            });
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new BackButton(Material.STAINED_GLASS_PANE, 14, backMenu));

        return buttons;
    }
}
