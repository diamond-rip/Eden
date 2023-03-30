package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.ArenaEditMenu;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.profile.procedure.Procedure;
import rip.diamond.practice.profile.procedure.ProcedureType;
import rip.diamond.practice.util.Common;

public class ArenaDisplayNameButton extends ArenaButton {
    public ArenaDisplayNameButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_DISPLAY_NAME_NAME.toString();
    }

    @Override
    public Material getIcon() {
        return Material.ANVIL;
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_DISPLAY_NAME_DESCRIPTION.toString(arena.getDisplayName());
    }

    @Override
    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_DISPLAY_NAME_ACTION_DESCRIPTION.toString();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.closeInventory();
        Procedure.buildProcedure(player, Language.ARENA_EDIT_MENU_DISPLAY_NAME_PROCEDURE_MESSAGE.toString(), ProcedureType.CHAT, (s) -> {
            String message = (String) s;

            arena.setDisplayName(message);
            Common.sendMessage(player, Language.ARENA_EDIT_MENU_DISPLAY_NAME_PROCEDURE_SUCCESS.toString(arena.getName(), arena.getDisplayName()));
            arena.autoSave();
            new ArenaEditMenu(arena).openMenu(player);
        });
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
