package rip.diamond.practice.arenas.menu.button.impl;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.ArenaAllowedKitsMenu;
import rip.diamond.practice.arenas.menu.button.ArenaButton;
import rip.diamond.practice.util.menu.Menu;

public class ArenaAllowedKitsButton extends ArenaButton {

    private final Menu backMenu;

    public ArenaAllowedKitsButton(Arena arena, Menu backMenu) {
        super(arena);
        this.backMenu = backMenu;
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
        new ArenaAllowedKitsMenu(arena, backMenu).openMenu(player);
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }
}
