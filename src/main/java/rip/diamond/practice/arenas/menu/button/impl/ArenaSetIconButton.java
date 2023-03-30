package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;

public class ArenaSetIconButton extends ArenaButton {
    public ArenaSetIconButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_ICON_NAME.toString();
    }

    @Override
    public Material getIcon() {
        return arena.getIcon().getType();
    }

    @Override
    public int getDurability() {
        return arena.getIcon().getDurability();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_ICON_DESCRIPTION.toString(getIcon().name() + ":" + getDurability());
    }

    @Override
    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_ICON_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            Language.ARENA_EDIT_MENU_ICON_CANNOT_BE_AIR.sendMessage(player);
            return;
        }
        arena.setIcon(itemStack);
        arena.autoSave();
    }
}
