package rip.diamond.practice.kits.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.impl.*;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;
import rip.diamond.practice.util.menu.button.BackButton;
import rip.diamond.practice.util.menu.pagination.PageButton;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitDetailsMenu extends PaginatedMenu {
    private final Kit kit;
    private final Menu backMenu;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.KIT_KIT_DETAIL_MENU_TITLE.toString(kit.getName());
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
        int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));
        int topIndex = 0;

        for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
            int index = entry.getKey();

            if (index >= minIndex && index < maxIndex) {
                index -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 27;
                buttons.put(index, entry.getValue());

                if (index > topIndex) {
                    topIndex = index;
                }
            }
        }

        buttons.put(18, new PageButton(-1, this));
        buttons.put(26, new PageButton(1, this));

        Map<Integer, Button> global = getGlobalButtons(player);
        if (global != null) {
            buttons.putAll(global);
        }

        return buttons;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        if (backMenu != null) {
            buttons.put(0, new BackButton(Material.STAINED_GLASS_PANE, 14, backMenu));
        }
        buttons.put(3, new KitEditMatchTypeButton(kit, this));
        buttons.put(4, new KitPreviewButton(kit, this));
        buttons.put(5, new KitToggleButton(kit, this));
        buttons.put(8, new KitSaveButton(kit, this));
        buttons.put(9, new KitEditDisplayNameButton(kit, this));
        buttons.put(10, new KitEditDescriptionButton(kit, this));
        buttons.put(11, new KitEditIconButton(kit, this));
        buttons.put(12, new KitEditPriorityButton(kit, this));
        buttons.put(13, new KitEditPotionEffectButton(kit, this));
        buttons.put(14, new KitEditDamageTicksButton(kit, this));
        buttons.put(15, new KitToggleRankedButton(kit, this));
        buttons.put(16, new KitSaveLoadoutButton(kit, this));
        buttons.put(17, new KitEditExtraItemsButton(kit));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Field field : kit.getGameRules().getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType() == boolean.class) {
                buttons.put(buttons.size(), new KitRulesToggleButton(this, kit, field));
            } else if (field.getType() == int.class) {
                buttons.put(buttons.size(), new KitRulesSetIntegerButton(this, kit, field));
            } else {
                buttons.put(buttons.size(), new KitRulesSetStringButton(this, kit, field));
            }
        }

        return buttons;
    }
}
