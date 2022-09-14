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
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_ICON_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;
            String[] args = message.split(";");

            if (!Checker.isMaterial(args[0]) || !Checker.isInteger(args[1])) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            Material material = Material.valueOf(args[0]);
            int durability = Integer.parseInt(args[1]);

            kit.setDisplayIcon(new ItemBuilder(material).durability(durability).build());
            Language.KIT_BUTTON_EDIT_ICON_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), message);
            new KitDetailsMenu(kit, null).openMenu(player);
        });
        Language.KIT_BUTTON_EDIT_ICON_PROCEDURE_ADDITIONAL_MESSAGE.sendMessage(player);
    }
}
