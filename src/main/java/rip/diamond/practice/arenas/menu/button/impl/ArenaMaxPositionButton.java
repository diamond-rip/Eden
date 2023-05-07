package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.List;

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
    public String getActionDescription() {
        return null;
    }

    @Override
    public List<String> getActionDescriptions() {
        return Language.ARENA_EDIT_MENU_MAX_ACTION_DESCRIPTION.toStringList();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        if (clickType == ClickType.LEFT) {
            player.closeInventory();
            Util.performCommand(player, "arena setup " + getArena().getName() + " max");
        } else if (clickType == ClickType.RIGHT) {
            Util.teleport(player, arena.getMax());
        }
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
