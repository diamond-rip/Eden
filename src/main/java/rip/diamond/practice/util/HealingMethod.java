package rip.diamond.practice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Language;

@Getter
@AllArgsConstructor
public enum HealingMethod {
    POTION(Language.MATCH_HEALING_METHOD_POTION.toString(), new ItemBuilder(Material.POTION).durability(16421).build().clone()),
    SOUP(Language.MATCH_HEALING_METHOD_SOUP.toString(), new ItemStack(Material.MUSHROOM_SOUP).clone()),
    GOLDEN_APPLE(Language.MATCH_HEALING_METHOD_GOLDEN_APPLE.toString(), new ItemStack(Material.GOLDEN_APPLE).clone());

    private final String name;
    private final ItemStack item;

    public static HealingMethod getHealingMethod(ItemStack[] contents) {
        for (ItemStack itemStack : contents) {
            if (itemStack == null) {
                continue;
            }
            if (itemStack.isSimilar(HealingMethod.POTION.getItem())) {
                return HealingMethod.POTION;
            } else if (itemStack.isSimilar(HealingMethod.SOUP.getItem())) {
                return HealingMethod.SOUP;
            } else if (itemStack.isSimilar(HealingMethod.GOLDEN_APPLE.getItem())) {
                return HealingMethod.GOLDEN_APPLE;
            }
        }
        return null;
    }
}