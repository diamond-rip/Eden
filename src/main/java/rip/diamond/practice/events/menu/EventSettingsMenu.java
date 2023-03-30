package rip.diamond.practice.events.menu;

import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.events.impl.SumoEvent;
import rip.diamond.practice.events.impl.Tournament;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;
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
        return Language.EVENT_EVENT_SETTINGS_MENU_TITLE.toString(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(buttons.size(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GHAST_TEAR)
                        .name(Language.EVENT_EVENT_SETTINGS_MENU_MAX_PLAYERS_BUTTON_NAME.toString(player))
                        .lore(Language.EVENT_EVENT_SETTINGS_MENU_MAX_PLAYERS_BUTTON_LORE.toStringList(player, maxPlayers))
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
                            .name(Language.EVENT_EVENT_SETTINGS_MENU_PARTY_SIZE_BUTTON_NAME.toString(player))
                            .lore(Language.EVENT_EVENT_SETTINGS_MENU_PARTY_SIZE_BUTTON_LORE.toStringList(player, teamSize))
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
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.BOOK)
                            .name(Language.EVENT_EVENT_SETTINGS_MENU_KIT_BUTTON_NAME.toString(player))
                            .lore(Language.EVENT_EVENT_SETTINGS_MENU_KIT_BUTTON_LORE.toStringList(player, kit.getDisplayName()))
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
                        .name(Language.EVENT_EVENT_SETTINGS_MENU_START_BUTTON_NAME.toString(player))
                        .lore(Language.EVENT_EVENT_SETTINGS_MENU_START_BUTTON_LORE.toStringList(player, eventType.getName(), maxPlayers, minPlayers))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();

                EdenEvent event = EdenEvent.getOnGoingEvent();
                if (event != null) {
                    Language.EVENT_EVENT_IS_RUNNING.sendMessage(player);
                    return;
                }

                switch (eventType) {
                    case TOURNAMENT:
                        Tournament tournament = new Tournament(player.getName(), minPlayers, maxPlayers, kit, teamSize);
                        tournament.create();
                        return;
                    case SUMO_EVENT:
                        SumoEvent sumoEvent = new SumoEvent(player.getName(), minPlayers, maxPlayers, teamSize);
                        sumoEvent.create();
                        return;
                    default:
                        throw new PracticeUnexpectedException("Event type " + eventType.getName() + " is not initialized yet");
                }
            }
        });
        return buttons;
    }
}
