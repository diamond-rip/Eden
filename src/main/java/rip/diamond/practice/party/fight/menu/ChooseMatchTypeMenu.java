package rip.diamond.practice.party.fight.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class ChooseMatchTypeMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return Language.PARTY_CHOOSE_MATCH_TYPE_MENU_TITLE.toString();
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(Language.PARTY_CHOOSE_MATCH_TYPE_MENU_SPLIT_BUTTON_NAME.toString())
                        .lore(Language.PARTY_CHOOSE_MATCH_TYPE_MENU_SPLIT_BUTTON_LORE.toStringList(player))
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new ChooseKitMenu(KitMatchType.SPLIT).openMenu(player);
            }
        });


        buttons.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GOLD_AXE)
                        .name(Language.PARTY_CHOOSE_MATCH_TYPE_MENU_FFA_BUTTON_NAME.toString())
                        .lore(Language.PARTY_CHOOSE_MATCH_TYPE_MENU_FFA_BUTTON_LORE.toStringList(player))
                        .build();
            }
            @Override
            public void clicked(Player player, ClickType clickType) {
                new ChooseKitMenu(KitMatchType.FFA).openMenu(player);
            }
        });


        return buttons;
    }
}
