package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.serialization.LocationSerialization;

public class ArenaMaxPositionButton extends ArenaButton {
    public ArenaMaxPositionButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_MAX_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_MAX_DESCRIPTION.toString(LocationSerialization.toReadable(getArena().getMax()));
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        Util.performCommand(player, "arena setup " + getArena().getName() + " max");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
