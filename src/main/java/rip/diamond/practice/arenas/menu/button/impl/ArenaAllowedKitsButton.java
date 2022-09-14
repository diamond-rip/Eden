package rip.diamond.practice.arenas.menu.button.impl;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.ArenaButton;

public class ArenaAllowedKitsButton extends ArenaButton {
    public ArenaAllowedKitsButton(Arena arena) {
        super(arena);
    }

    @Override
    public String getName() {
        return Language.ARENA_EDIT_MENU_ALLOWED_KITS_NAME.toString();
    }

    @Override
    public String getDescription() {
        return Language.ARENA_EDIT_MENU_ALLOWED_KITS_DESCRIPTION.toString(StringUtils.join(getArena().getAllowedKits(), ", "));
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        player.closeInventory();
        player.performCommand("arena setup " + getArena().getName() + " allowed-kits");
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
