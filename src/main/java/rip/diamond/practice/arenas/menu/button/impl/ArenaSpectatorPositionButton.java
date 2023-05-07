package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.List;

public class ArenaSpectatorPositionButton extends ArenaButton {
    public ArenaSpectatorPositionButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_SPECTATOR_POSITION_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_SPECTATOR_POSITION_DESCRIPTION.toString(LocationSerialization.toReadable(getArena().getSpectator()));
    }

    @Override
    public String getActionDescription() {
        return null;
    }

    @Override
    public List<String> getActionDescriptions() {
        return Language.ARENA_EDIT_MENU_SPECTATOR_POSITION_ACTION_DESCRIPTION.toStringList();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        if (clickType == ClickType.LEFT) {
            Util.performCommand(player, "arena setup " + getArena().getName() + " spectator");
        } else if (clickType == ClickType.RIGHT) {
            Util.teleport(player, arena.getSpectator());
        }
    }
}
