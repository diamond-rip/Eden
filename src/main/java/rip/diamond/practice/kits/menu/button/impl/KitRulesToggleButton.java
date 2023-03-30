package rip.diamond.practice.kits.menu.button.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.util.menu.button.ToggleButton;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class KitRulesToggleButton extends ToggleButton {

    private final KitDetailsMenu menu;
    private final Kit kit;
    private final Field field;

    @Override
    public String getOptionName() {
        return KitGameRules.Readable.valueOf(field.getName()).getRule();
    }

    @Override
    public String getDescription() {
        return KitGameRules.Readable.valueOf(field.getName()).getDescription();
    }

    @SneakyThrows
    @Override
    public boolean isEnabled(Player player) {
        return field.getBoolean(kit.getGameRules());
    }

    @SneakyThrows
    @Override
    public void onClick(Player player, int slot, ClickType clickType, int hotbarSlot) {
        field.setBoolean(kit.getGameRules(), !isEnabled(player));
        Language.KIT_BUTTON_RULES_TOGGLE_SUCCESS.sendMessage(player, kit.getName(), getOptionName(), (isEnabled(player) ? Language.ENABLED.toString() : Language.DISABLED.toString()));
        kit.autoSave();
        menu.openMenu(player);
    }
}
