package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.serialization.LocationSerialization;

public class ArenaAPositionButton extends ArenaButton {
    public ArenaAPositionButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_A_POSITION_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_A_POSITION_DESCRIPTION.toString(LocationSerialization.toReadable(getArena().getA()));
    }

    @Override
    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_A_POSITION_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.performCommand("arena setup " + getArena().getName() + " a");
    }
}
