package rip.diamond.practice.kits.menu.button.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.menu.button.ToggleButton;

@RequiredArgsConstructor
public class KitToggleRankedButton extends ToggleButton {

    private final Kit kit;
    private final KitDetailsMenu menu;

    @Override
    public String getOptionName() {
        return Language.KIT_BUTTON_TOGGLE_RANKED_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.KIT_BUTTON_TOGGLE_RANKED_LORE.toString((kit.isRanked() ? Language.ENABLED.toString() : Language.DISABLED.toString()));
    }

    @Override
    public boolean isEnabled(Player player) {
        return kit.isRanked();
    }

    @Override
    public void onClick(Player player, int slot, ClickType clickType, int hotbarSlot) {
        kit.setRanked(!kit.isRanked());
        Language.KIT_BUTTON_TOGGLE_RANKED_SUCCESS.sendMessage(player, kit.getName(), (isEnabled(player) ? CC.GREEN + Language.ENABLED.toString() : CC.RED + Language.DISABLED.toString()));
        kit.autoSave();
        menu.openMenu(player);
    }
}
