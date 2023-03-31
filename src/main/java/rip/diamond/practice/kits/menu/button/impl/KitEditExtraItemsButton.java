package rip.diamond.practice.kits.menu.button.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.button.KitButton;
import rip.diamond.practice.util.ItemBuilder;

public class KitEditExtraItemsButton extends KitButton {
    public KitEditExtraItemsButton(Kit kit) {
        super(kit);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.ENDER_CHEST)
                .name(Language.KIT_BUTTON_EDIT_EXTRA_ITEMS_NAME.toString())
                .lore(Language.KIT_BUTTON_EDIT_EXTRA_ITEMS_LORE.toStringList(player))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, kit.getName() + "的額外物品");
        kit.getKitExtraItems().forEach(item -> {
            ItemBuilder builder = new ItemBuilder(item.getMaterial()).amount(item.getAmount()).durability(item.getData()).enchantments(item.getEnchantments());
            if (item.getName() != null) {
                builder.name(item.getName());
            }
            if (item.isUnbreakable()) {
                builder.unbreakable();
            }
            inventory.addItem(builder.build());
        });
        player.openInventory(inventory);
    }
}
