package rip.diamond.practice.arenas.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

public class ArenasMenu extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.ARENA_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Arena arena : Arena.getArenas()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(arena.getIcon())
                            .name(Language.ARENA_MENU_ARENA_EDIT_NAME.toString(arena.getDisplayName(), arena.getName()))
                            .lore(Language.ARENA_MENU_ARENA_EDIT_LORE.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    Util.performCommand(player, "arena edit " + arena.getName());
                }
            });
        }

        return buttons;
    }
}
