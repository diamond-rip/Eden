package rip.diamond.practice.kits;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

@Data
public class KitExtraItem {

    private String name;
    private Material material;
    private int amount;
    private int data;
    private boolean unbreakable;
    private Map<String, Integer> enchantments;

    public Map<Enchantment, Integer> getEnchantments() {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        this.enchantments.forEach((string, integer) -> {
            enchantments.put(Enchantment.getByName(string), integer);
        });
        return enchantments;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        Map<String, Integer> enchantmentsMap = new HashMap<>();
        enchantments.forEach((enchantment, integer) -> {
            enchantmentsMap.put(enchantment.getName(), integer);
        });
        this.enchantments = enchantmentsMap;
    }
}
