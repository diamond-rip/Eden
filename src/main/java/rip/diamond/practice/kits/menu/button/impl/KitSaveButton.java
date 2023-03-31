package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.menu.Menu;
import rip.diamond.practice.util.menu.menus.ConfirmMenu;

public class KitSaveButton extends KitButton {

    private final Menu backMenu;

    public KitSaveButton(Kit kit, Menu backMenu) {
        super(kit);
        this.backMenu = backMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.INK_SACK)
                .durability(10)
                .name(Language.KIT_BUTTON_SAVE_NAME.toString())
                .lore(Language.KIT_BUTTON_SAVE_LORE.toStringList(player))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        new ConfirmMenu((bool) -> {
            if (bool) {
                Util.performCommand(player, "kit save " + kit.getName());
            }
            backMenu.openMenu(player);
        }, false, null).openMenu(player);
    }
}
