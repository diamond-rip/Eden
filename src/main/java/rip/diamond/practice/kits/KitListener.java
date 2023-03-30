package rip.diamond.practice.kits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.util.Tasks;

import java.util.Arrays;

public class KitListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getView().getTopInventory();
        if (inventory.getTitle().endsWith("的額外物品")) {
            String kitName = inventory.getTitle().replaceAll("的額外物品", "");
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                Language.KIT_CANNOT_SAVE_EXTRA_ITEMS.sendMessage(player, kitName);
                return;
            }
            kit.getKitExtraItems().clear();
            Arrays.stream(inventory.getContents()).forEach(item -> {
                if (item == null) {
                    return;
                }
                boolean hasMeta = item.hasItemMeta();
                KitExtraItem kitExtraItem = new KitExtraItem();
                kitExtraItem.setName(hasMeta ? item.getItemMeta().getDisplayName() : null);
                kitExtraItem.setMaterial(item.getType());
                kitExtraItem.setAmount(item.getAmount());
                kitExtraItem.setData(item.getDurability());
                kitExtraItem.setUnbreakable(hasMeta && item.getItemMeta().spigot().isUnbreakable());
                kitExtraItem.setEnchantments(item.getEnchantments());
                kit.getKitExtraItems().add(kitExtraItem);
            });
            Language.KIT_SUCCESSFULLY_UPDATED_EXTRA_ITEMS.sendMessage(player, kit.getName());
            kit.autoSave();
            Tasks.runLater(()-> new KitDetailsMenu(kit, null).openMenu(player), 1L);
        }
    }

}
