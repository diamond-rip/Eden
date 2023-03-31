package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Menu;

public class KitEditMatchTypeButton extends KitButton {

    private final Menu menu;

    public KitEditMatchTypeButton(Kit kit, Menu menu) {
        super(kit);
        this.menu = menu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.FLINT)
                .name(Language.KIT_BUTTON_EDIT_MATCH_TYPE_NAME.toString())
                .lore(
                        "",
                        (kit.getKitMatchTypes().contains(KitMatchType.SOLO) ? CC.GREEN + " » " : CC.GRAY + "   ") + "Solo " + Language.KIT_BUTTON_EDIT_MATCH_TYPE_LORE_CLICK_LEFT.toString(),
                        (kit.getKitMatchTypes().contains(KitMatchType.FFA) ? CC.GREEN + " » " : CC.GRAY + "   ") + "FFA " + Language.KIT_BUTTON_EDIT_MATCH_TYPE_LORE_CLICK_MIDDLE.toString(),
                        (kit.getKitMatchTypes().contains(KitMatchType.SPLIT) ? CC.GREEN + " » " : CC.GRAY + "   ") + "Split " + Language.KIT_BUTTON_EDIT_MATCH_TYPE_LORE_CLICK_RIGHT.toString()
                )
                .lore(Language.KIT_BUTTON_EDIT_MATCH_TYPE_LORE.toStringList(player))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        playNeutral(player);
        switch (clickType) {
            case LEFT:
                if (kit.getKitMatchTypes().contains(KitMatchType.SOLO)) {
                    kit.getKitMatchTypes().remove(KitMatchType.SOLO);
                } else {
                    kit.getKitMatchTypes().add(KitMatchType.SOLO);
                }
                break;
            case MIDDLE:
                if (kit.getKitMatchTypes().contains(KitMatchType.FFA)) {
                    kit.getKitMatchTypes().remove(KitMatchType.FFA);
                } else {
                    kit.getKitMatchTypes().add(KitMatchType.FFA);
                }
                break;
            case RIGHT:
                if (kit.getKitMatchTypes().contains(KitMatchType.SPLIT)) {
                    kit.getKitMatchTypes().remove(KitMatchType.SPLIT);
                } else {
                    kit.getKitMatchTypes().add(KitMatchType.SPLIT);
                }
                break;
            default:
                break;
        }
        kit.autoSave();
        menu.openMenu(player);
    }
}
