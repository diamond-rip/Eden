package rip.diamond.practice.kits.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.util.InventoryUtil;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;
import rip.diamond.practice.util.menu.button.BackButton;
import rip.diamond.practice.util.menu.button.DisplayButton;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class KitPreviewMenu extends Menu {

    private final Kit kit;
    private final Menu backMenu;

    @Override
    public String getTitle(Player player) {
        return Language.KIT_KIT_PREVIEW_MENU_TITLE.toString(kit.getName());
    }

    @Override
    public int getSize() {
        return 9*6;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        if (backMenu != null) {
            buttons.put(0, new BackButton(Material.STAINED_GLASS_PANE, 14, backMenu));
        }
        for (int i = 0; i < 9; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, placeholderButton);
            }
        }
        int i = 9;
        for (ItemStack itemStack : InventoryUtil.fixInventoryOrder(kit.getKitLoadout().getContents())) {
            if (itemStack != null && itemStack.getType() != null && itemStack.getType() != Material.AIR) {
                buttons.put(i++, new DisplayButton(itemStack, true));
            }
        }
        int x = 0;
        for (ItemStack itemStack : kit.getKitLoadout().getArmor()) {
            if (itemStack != null && itemStack.getType() != null && itemStack.getType() != Material.AIR) {
                buttons.put(48 - x++, new DisplayButton(itemStack, true));
            }
        }
        return buttons;
    }
}
