package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Menu;

public class KitEditDisplayNameButton extends KitButton {

    private final Menu backMenu;

    public KitEditDisplayNameButton(Kit kit, Menu backMenu) {
        super(kit);
        this.backMenu = backMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ANVIL)
                .name(Language.KIT_BUTTON_EDIT_DISPLAY_NAME_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_DISPLAY_NAME_LORE.toStringList(player, kit.getDisplayName()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_DISPLAY_NAME_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            kit.setDisplayName(message);
            Common.sendMessage(player, Language.KIT_BUTTON_EDIT_DISPLAY_NAME_PROCEDURE_SUCCESS.toString(kit.getName(), kit.getDisplayName()));
            kit.autoSave();
            backMenu.openMenu(player);
        });
    }
}
