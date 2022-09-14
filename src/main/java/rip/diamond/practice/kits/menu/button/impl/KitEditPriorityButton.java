package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.ItemBuilder;

public class KitEditPriorityButton extends KitButton {

    private final Kit kit;

    public KitEditPriorityButton(Kit kit) {
        super(kit);
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.GHAST_TEAR)
                .name(Language.KIT_BUTTON_EDIT_PRIORITY_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_PRIORITY_LORE.toStringList(player, kit.getPriority()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_PRIORITY_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            if (!Checker.isInteger(message)) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            int priority = Integer.parseInt(message);

            kit.setPriority(priority);
            Language.KIT_BUTTON_EDIT_PRIORITY_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), kit.getPriority());
            new KitDetailsMenu(kit, null).openMenu(player);
        });

        Language.KIT_BUTTON_EDIT_PRIORITY_PROCEDURE_ADDITIONAL_MESSAGE.sendMessage(player);
    }
}
