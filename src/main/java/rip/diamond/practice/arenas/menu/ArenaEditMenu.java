package rip.diamond.practice.arenas.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.menu.button.impl.*;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ArenaEditMenu extends Menu {

    private final Arena arena;

    @Override
    public String getTitle(Player player) {
        return Language.ARENA_EDIT_MENU_TITLE.toString(arena.getName());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new ArenaCloneButton(arena));
        buttons.put(1, new ArenaToggleButton(arena));
        buttons.put(3, new ArenaDisplayNameButton(arena));
        buttons.put(5, new ArenaSetIconButton(arena));
        buttons.put(7, new ArenaSaveButton(arena));
        buttons.put(8, new ArenaDeleteButton(arena));

        buttons.put(18, new ArenaAPositionButton(arena));
        buttons.put(19, new ArenaBPositionButton(arena));
        buttons.put(20, new ArenaSpectatorPositionButton(arena));
        buttons.put(21, new ArenaMinPositionButton(arena));
        buttons.put(22, new ArenaMaxPositionButton(arena));
        buttons.put(23, new ArenaBuildMaxButton(arena));
        buttons.put(24, new ArenaYLimitButton(arena));
        buttons.put(25, new ArenaPortalProtectionRadiusButton(arena));
        buttons.put(26, new ArenaAllowedKitsButton(arena, this));

        return buttons;
    }
}
