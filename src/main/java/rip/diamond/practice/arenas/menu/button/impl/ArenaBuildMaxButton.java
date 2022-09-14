package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;

public class ArenaBuildMaxButton extends ArenaButton {
    public ArenaBuildMaxButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_BUILD_MAX_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_BUILD_MAX_DESCRIPTION.toString(getArena().getBuildMax());
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        player.performCommand("arena setup " + getArena().getName() + " build-max");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
