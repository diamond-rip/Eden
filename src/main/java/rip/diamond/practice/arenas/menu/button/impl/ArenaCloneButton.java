package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.ArenaDetailsMenu;
import rip.diamond.practice.arenas.menu.button.ArenaButton;

public class ArenaCloneButton extends ArenaButton {

    public ArenaCloneButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_CLONE_NAME.toString();
    }

    @Override
    public Material getIcon() {
        return Material.GRASS;
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_CLONE_DESCRIPTION.toString();
    }

    @Override
    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_CLONE_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        new ArenaDetailsMenu(arena).openMenu(player);
    }
}
