package rip.diamond.practice.arenas.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.arenas.menu.button.impl.ArenaDetailButton;
import rip.diamond.practice.arenas.task.ArenaGenerateTask;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ArenaDetailsMenu extends PaginatedMenu {
    private final Arena arena;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.ARENA_DETAILS_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (ArenaDetail arenaDetail : arena.getArenaDetails()) {
            buttons.put(buttons.size(), new ArenaDetailButton(arena, arenaDetail, i++));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GRASS)
                        .name(Language.ARENA_DETAILS_MENU_CREATE_DUPLICATE_NAME.toString())
                        .lore(Language.ARENA_DETAILS_MENU_CREATE_DUPLICATE_LORE.toStringList(player, arena.getName()))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();
                new ArenaGenerateTask(player, arena);
            }
        });

        return buttons;
    }
}
