package rip.diamond.practice.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder implements Listener {

	private ItemStack is;

	public ItemBuilder(Material mat) {
		is = new ItemStack(mat);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;
	}

	public ItemBuilder amount(int amount) {
		is.setAmount(amount);
		return this;
	}

	public ItemBuilder name(String name) {
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(String name) {
		ItemMeta meta = is.getItemMeta();
		List<String> lore = meta.getLore();

		if (lore == null) {
			lore = new ArrayList<>();
		}

		if (name != null) {
			lore.add(ChatColor.translateAlternateColorCodes('&', name));
		}
		meta.setLore(lore);

		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder lore(String... lore) {
		ItemMeta meta = is.getItemMeta();
		List<String> toSet = meta.getLore();

		if (toSet == null) {
			toSet = new ArrayList<>();
		}

		for (String string : lore) {
			if (string != null) {
				toSet.add(ChatColor.translateAlternateColorCodes('&', string));
			}
		}

		meta.setLore(toSet);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		ItemMeta meta = is.getItemMeta();
		List<String> toSet = meta.getLore();

		if (toSet == null) {
			toSet = new ArrayList<>();
		}

		for (String string : lore) {
			if (string != null) {
				toSet.add(ChatColor.translateAlternateColorCodes('&', string));
			}
		}

		meta.setLore(toSet);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder durability(int durability) {
		is.setDurability((short) durability);
		return this;
	}

	public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
		this.is.addUnsafeEnchantments(enchantments);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		is.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment) {
		is.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder type(Material material) {
		is.setType(material);
		return this;
	}

	public ItemBuilder clearLore() {
		ItemMeta meta = is.getItemMeta();

		meta.setLore(new ArrayList<>());
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder clearEnchantments() {
		for (Enchantment e : is.getEnchantments().keySet()) {
			is.removeEnchantment(e);
		}

		return this;
	}

	public ItemBuilder unbreakable() {
		ItemMeta im = this.is.getItemMeta();
		im.spigot().setUnbreakable(true);
		this.is.setItemMeta(im);
		return this;
	}

	public ItemBuilder hideItemFlags() {
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder glow() {
		is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addNBTTag(String s1, String s2) {
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		compound.set(s1, new NBTTagString(s2));
		is = CraftItemStack.asBukkitCopy(nmsItem);
		return this;
	}

	public ItemBuilder headTexture(String texture) {
		if (texture != null) {
			SkullMeta hm = (SkullMeta) is.getItemMeta();
			GameProfile profile = new GameProfile(new UUID(texture.hashCode(), texture.hashCode()), null);
			profile.getProperties().put("textures", new Property("Value", texture));

			try{
				Field profileField = hm.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(hm, profile);
			} catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			is.setItemMeta(hm);
		}
		return this;
	}

	public ItemBuilder skull(String owner) {
		try {
			SkullMeta im = (SkullMeta) is.getItemMeta();
			im.setOwner(owner);
			is.setItemMeta(im);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	public ItemStack build() {
		return is;
	}

}