package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.menu.Menu;

import java.util.stream.Collectors;

public class KitEditPotionEffectButton extends KitButton {

    private final Menu backMenu;

    public KitEditPotionEffectButton(Kit kit, Menu backMenu) {
        super(kit);
        this.backMenu = backMenu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.POTION)
                .durability(8193)
                .name(Language.KIT_BUTTON_EDIT_POTION_EFFECT_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_POTION_EFFECT_LORE_START.toStringList(player, kit.getEffects().size()))
                .lore(kit.getEffects().stream().map(effect -> " " + CC.DARK_AQUA + WordUtil.formatWords(effect.getType().getName()) + " " + (effect.getAmplifier() + 1) + CC.GRAY + " - " + CC.WHITE + TimeUtil.millisToTimer(effect.getDuration() / 20 * 1000L)).collect(Collectors.toList()))
                .lore(Language.KIT_BUTTON_EDIT_POTION_EFFECT_LORE_END.toStringList(player))
                .hideItemFlags()
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.KIT_BUTTON_EDIT_POTION_EFFECT_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;
            String[] args = message.split(";");

            if (!Checker.isPotionEffect(args[0])) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            PotionEffectType effect = PotionEffectType.getByName(args[0].replace("-", ""));

            if (message.startsWith("-")) {
                kit.getEffects().removeIf(potionEffect -> potionEffect.getType() == effect);
                Language.KIT_BUTTON_EDIT_POTION_EFFECT_PROCEDURE_SUCCESS_REMOVE.sendMessage(player, kit.getName(), WordUtil.toCapital(effect.getName()));
                return;
            } else {
                if (!Checker.isInteger(args[1]) || !Checker.isInteger(args[2])) {
                    Language.INVALID_SYNTAX.sendMessage(player);
                    return;
                }

                int amplifier = Integer.parseInt(args[1]);
                int duration = Integer.parseInt(args[2]);

                if (kit.getEffects().stream().anyMatch(potionEffect -> potionEffect.getType() == effect)) {
                    Language.KIT_BUTTON_EDIT_POTION_EFFECT_PROCEDURE_ALREADY_HAVE_POTION_EFFECT.sendListOfMessage(player, kit.getName(), WordUtil.toCapital(effect.getName()));
                    return;
                }

                kit.getEffects().add(new PotionEffect(effect, duration, amplifier));
                Language.KIT_BUTTON_EDIT_POTION_EFFECT_PROCEDURE_SUCCESS_ADD.sendMessage(player, kit.getName(), WordUtil.toCapital(effect.getName()) + " " + (amplifier + 1) + " (" + TimeUtil.millisToTimer(duration / 20 * 1000L) + ")");
            }
            kit.autoSave();
            backMenu.openMenu(player);
        });

        Language.KIT_BUTTON_EDIT_POTION_EFFECT_PROCEDURE_ADDITIONAL_MESSAGE.sendListOfMessage(player);
    }

}
