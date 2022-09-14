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

public class KitEditDamageTicksButton extends KitButton {

    private final Kit kit;

    public KitEditDamageTicksButton(Kit kit) {
        super(kit);
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.NETHER_STAR)
                .name(Language.KIT_BUTTON_EDIT_DAMAGE_TICKS_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_DAMAGE_TICKS_LORE.toStringList(player, kit.getDamageTicks()))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_DAMAGE_TICKS_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            if (!Checker.isInteger(message)) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            int damageTicks = Integer.parseInt(message);

            kit.setDamageTicks(damageTicks);
            Language.KIT_BUTTON_EDIT_DAMAGE_TICKS_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), kit.getDamageTicks());
            new KitDetailsMenu(kit, null).openMenu(player);
        });

        Language.KIT_BUTTON_EDIT_DAMAGE_TICKS_PROCEDURE_ADDITIONAL_MESSAGE.sendMessage(player);
    }
}
