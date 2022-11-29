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

public class KitEditIconButton extends KitButton {

    private final Kit kit;

    public KitEditIconButton(Kit kit) {
        super(kit);
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.EYE_OF_ENDER)
                .name(Language.KIT_BUTTON_EDIT_ICON_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_ICON_LORE.toStringList(getKit().getDisplayIcon().getType().name(), getKit().getDisplayIcon().getDurability()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();

        ItemStack itemStack = player.getItemInHand().clone();
        if (itemStack.getType() == Material.AIR) {
            Language.KIT_BUTTON_EDIT_ICON_PROCEDURE_AIR.sendMessage(player);
        } else {
            kit.setDisplayIcon(itemStack);
            Language.KIT_BUTTON_EDIT_ICON_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), itemStack.getType().name());
        }
        new KitDetailsMenu(kit, null).openMenu(player);
    }
}
