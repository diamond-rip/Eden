package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.arenas.task.ArenaRemoveTask;
import rip.diamond.practice.util.menu.menus.ConfirmMenu;

public class ArenaDeleteButton extends ArenaButton {

    public ArenaDeleteButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_DELETE_NAME.toString();
    }

    @Override
    public Material getIcon() {
        return Material.REDSTONE;
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_DELETE_DESCRIPTION.toString();
    }

    @Override
    public String getActionDescription() {
        return arena.hasClone() ? Language.ARENA_EDIT_MENU_DELETE_ACTION_DESCRIPTION_HAS_CLONE.toString() : Language.ARENA_EDIT_MENU_DELETE_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (arena.hasClone()) {
            Language.ARENA_EDIT_MENU_DELETE_ACTION_DESCRIPTION_HAS_CLONE.sendMessage(player);
            return;
        }

        new ConfirmMenu((bool) -> {
            new ArenaRemoveTask(player, arena, arena.getArenaDetails().get(0));
        }, true, null).openMenu(player);
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
