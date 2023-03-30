package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.Util;

public class ArenaSaveButton extends ArenaButton {
    public ArenaSaveButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_SAVE_NAME.toString();
    }

    @Override
    public Material getIcon() {
        return Material.EMERALD;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_SAVE_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        Util.performCommand(player, "arena save " + getArena().getName());
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
