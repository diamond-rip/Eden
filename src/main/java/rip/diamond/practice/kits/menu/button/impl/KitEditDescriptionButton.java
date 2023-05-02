package rip.diamond.practice.kits.menu.button.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Menu;

import java.util.Arrays;

public class KitEditDescriptionButton extends KitButton {

    private final Menu backMenu;

    public KitEditDescriptionButton(Kit kit, Menu backMenu) {
        super(kit);
        this.backMenu = backMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .name(Language.KIT_BUTTON_EDIT_DESCRIPTION_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_DESCRIPTION_LORE_START.toStringList(player))
                .lore(kit.getDescription())
                .lore(Language.KIT_BUTTON_EDIT_DESCRIPTION_LORE_END.toStringList(player))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        if (clickType == ClickType.DROP) {
            kit.setDescription(Lists.newArrayList());
            backMenu.openMenu(player);
            return;
        }
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_DESCRIPTION_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            kit.setDescription(Arrays.asList(message.split(";")));
            Language.KIT_BUTTON_EDIT_DESCRIPTION_PROCEDURE_SUCCESS.sendMessage(player, kit.getName(), StringUtils.join(kit.getDescription(), ", "));
            kit.autoSave();
            backMenu.openMenu(player);
        });
        Language.KIT_BUTTON_EDIT_DESCRIPTION_PROCEDURE_ADDITIONAL_MESSAGE.sendMessage(player);
    }
}
