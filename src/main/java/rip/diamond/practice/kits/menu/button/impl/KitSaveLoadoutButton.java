package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.menus.ConfirmMenu;

public class KitSaveLoadoutButton extends KitButton {

    public KitSaveLoadoutButton(Kit kit) {
        super(kit);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.CHEST)
                .name(Language.KIT_BUTTON_SAVE_LOADOUT_NAME.toString())
                .lore(Language.KIT_BUTTON_SAVE_LOADOUT_LORE.toStringList())
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        new ConfirmMenu((bool) -> {
            if (bool) {
                getKit().getKitLoadout().setArmor(player.getInventory().getArmorContents());
                getKit().getKitLoadout().setContents(player.getInventory().getContents());
                getKit().save(true);
                Language.KIT_BUTTON_SAVE_LOADOUT_SUCCESS.sendMessage(player);
            }
            new KitDetailsMenu(getKit(), null).openMenu(player);
        }, true, null).openMenu(player);
    }
}
