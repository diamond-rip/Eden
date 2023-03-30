package rip.diamond.practice.events.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class EventCreateMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return Language.EVENT_EVENT_CREATE_MENU_TITLE.toString(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (EventType eventType : EventType.values()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(eventType.getLogo())
                            .name(Language.EVENT_EVENT_CREATE_MENU_BUTTON_NAME.toString(player, eventType.getName()))
                            .lore(
                                    "",
                                    player.hasPermission(eventType.getPermission()) ? Language.EVENT_EVENT_CREATE_MENU_BUTTON_LORE_CLICK_TO_CREATE_EVENT.toString(player) : Language.EVENT_EVENT_CREATE_MENU_BUTTON_LORE_NO_PERMISSION.toString(player)
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    if (!player.hasPermission(eventType.getPermission())) {
                        Language.EVENT_EVENT_CREATE_MENU_BUTTON_LORE_NO_PERMISSION.sendMessage(player);
                        return;
                    }
                    new EventSettingsMenu(eventType).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
