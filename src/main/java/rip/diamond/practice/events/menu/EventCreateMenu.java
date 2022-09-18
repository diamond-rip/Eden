package rip.diamond.practice.events.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class EventCreateMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "建立活動";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (EventType eventType : EventType.values()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(eventType.getLogo())
                            .name("&b" + eventType.getName())
                            .lore(
                                    "",
                                    "&e&n點擊進入活動設置界面!"
                            )
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    new EventSettingsMenu(eventType).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
