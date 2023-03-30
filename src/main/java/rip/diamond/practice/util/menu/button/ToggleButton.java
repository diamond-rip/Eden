package rip.diamond.practice.util.menu.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;

public abstract class ToggleButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(isEnabled(player) ? Material.REDSTONE_TORCH_ON : Material.LEVER)
                .name(Language.BUTTON_TOGGLE_NAME.toString(getOptionName()))
                .lore("", CC.GRAY + getDescription(), "", CC.GREEN + (isEnabled(player) ? " » " : "   ") + Language.ENABLED.toString(), CC.RED + (!isEnabled(player) ? " » " : "   ") + Language.DISABLED.toString(), "")
                .build();
    }

    public abstract String getOptionName();

    public abstract String getDescription();

    public abstract boolean isEnabled(Player player);

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        playNeutral(player);
        onClick(player, slot, clickType, hotbarSlot);
    }

    public abstract void onClick(Player player, int slot, ClickType clickType, int hotbarSlot);
}
