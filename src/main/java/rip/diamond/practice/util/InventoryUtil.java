package rip.diamond.practice.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;

import java.util.Iterator;

public class InventoryUtil {

	public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
		ItemStack[] fixed = new ItemStack[36];

		System.arraycopy(source, 0, fixed, 27, 9);
		System.arraycopy(source, 9, fixed, 0, 27);

		return fixed;
	}

	public static void handleRemoveCrafting() {
		if (!Config.CRAFTING_ENABLED.toBoolean()) {
			Iterator<Recipe> iterator = Eden.INSTANCE.getServer().recipeIterator();

			while (iterator.hasNext()) {
				Recipe recipe = iterator.next();
				if (recipe != null && !Config.CRAFTING_WHITELISTED_ITEMS.toStringList().contains(recipe.getResult().getType().name())) {
					iterator.remove();
				}
			}
		}
	}

}
