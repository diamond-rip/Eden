package rip.diamond.practice.party.fight.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ChooseArenaMenu extends PaginatedMenu {
    private final KitMatchType kitMatchType;
    private final Kit kit;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.PARTY_CHOOSE_ARENA_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Arena arena : Arena.getArenas()) {
            ArenaDetail arenaDetail = Arena.getArenaDetail(arena);
            if (arena.isEnabled() && !arena.isLocked() && arenaDetail != null && arena.getAllowedKits().contains(kit.getName())) {
                buttons.put(buttons.size(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(arena.getIcon().clone())
                                .name(Language.PARTY_CHOOSE_ARENA_MENU_BUTTON_NAME.toString(arena.getDisplayName()))
                                .lore(Language.PARTY_CHOOSE_ARENA_MENU_BUTTON_LORE.toStringList(player))
                                .build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        player.closeInventory();
                        plugin.getPartyFightManager().startPartyEvent(player, kitMatchType, kit, arena);
                    }
                });
            }
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.MAP)
                        .name(Language.PARTY_CHOOSE_ARENA_MENU_BUTTON_RANDOM.toString())
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                Arena arena = Arena.getEnabledArena(kit);
                if (arena == null) {
                    Common.log("[Eden] There's no available arenas for kit " + kit.getName() + ", consider add more arenas.");
                    return;
                }

                player.closeInventory();
                plugin.getPartyFightManager().startPartyEvent(player, kitMatchType, kit, arena);
            }
        });

        return buttons;
    }
}
