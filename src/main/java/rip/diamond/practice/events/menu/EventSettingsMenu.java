package rip.diamond.practice.events.menu;

import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.events.impl.Tournament;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class EventSettingsMenu extends Menu {
    private final EventType eventType;
    private int maxPlayers;
    private int minPlayers;
    private int teamSize = 1;
    @Setter private Kit kit = Kit.getKits().get(0);

    public EventSettingsMenu(EventType eventType) {
        this.eventType = eventType;
        this.maxPlayers = eventType.getDefaultMaxPlayers();
        this.minPlayers = eventType.getDefaultMinPlayers();
    }

    @Override
    public String getTitle(Player player) {
        return "活動設置";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(buttons.size(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GHAST_TEAR)
                        .name("&b最大人數")
                        .lore(
                                "",
                                "&f現時最大人數: &b" + maxPlayers,
                                "",
                                "&a左鍵&f點擊把最大人數提升 1",
                                "&aShift + 左鍵&f點擊把最大人數提升 10",
                                "",
                                "&c右鍵&f點擊把最大人數降低 1",
                                "&cShift + 右鍵&f點擊把最大人數降低 10",
                                "",
                                "&e&n點擊更改最大人數!"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                switch (clickType) {
                    case LEFT:
                        maxPlayers += 1;
                        break;
                    case SHIFT_LEFT:
                        maxPlayers += 10;
                        break;
                    case RIGHT:
                        maxPlayers -= 1;
                        break;
                    case SHIFT_RIGHT:
                        maxPlayers -= 10;
                        break;
                }
                if (maxPlayers < 2) {
                    maxPlayers = 2;
                }
                openMenu(player);
            }
        });
        if (eventType.isAllowTeams()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM)
                            .durability(3)
                            .name("&b隊伍人數")
                            .lore(
                                    "",
                                    "&f現時隊伍人數: &b" + teamSize,
                                    "",
                                    "&a左鍵&f點擊把隊伍人數提升 1",
                                    "&c右鍵&f點擊把最大人數降低 1",
                                    "",
                                    "&e&n點擊更改隊伍人數!"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    switch (clickType) {
                        case LEFT:
                            teamSize += 1;
                            break;
                        case RIGHT:
                            teamSize -= 1;
                            break;
                    }
                    if (teamSize < 1) {
                        teamSize = 1;
                    }
                    openMenu(player);
                }
            });
        }
        if (eventType.isKit()) {
            buttons.put(1, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BOOK)
                            .name("&b職業選擇")
                            .lore(
                                    "",
                                    "&f已選擇職業: &b" + kit.getDisplayName(),
                                    "",
                                    "&e&n點擊選擇一個職業!"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    new EventSelectKitMenu(EventSettingsMenu.this).openMenu(player);
                }
            });
        }

        buttons.put(22, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(5)
                        .name("&b開始活動")
                        .lore(
                                "",
                                "&f活動: &b" + eventType.getName(),
                                "&f最大人數: &b" + maxPlayers,
                                "&f最小人數: &b" + minPlayers,
                                "",
                                "&e&n點擊開始活動!"
                        )
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();
                switch (eventType) {
                    case TOURNAMENT:
                        Tournament tournament = new Tournament(player.getName(), minPlayers, maxPlayers, kit, teamSize);
                        tournament.create();
                        return;
                }
            }
        });
        return buttons;
    }
}
