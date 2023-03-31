package rip.diamond.practice.kits;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.util.GsonType;
import rip.diamond.practice.util.serialization.BukkitSerialization;
import rip.diamond.practice.util.serialization.EffectSerialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Kit {

	@Getter private static final List<Kit> kits = new ArrayList<>();

	@Getter @Setter private String name;
	@Getter @Setter private String displayName;
	@Getter @Setter private int priority;
	@Getter @Setter private int damageTicks = 19;
	@Getter @Setter private boolean enabled;
	@Getter @Setter private boolean ranked;
	@Setter private ItemStack displayIcon;
	@Getter @Setter private List<String> description = new ArrayList<>();
	@Getter @Setter private Collection<PotionEffect> effects = new ArrayList<>();
	@Getter private final KitLoadout kitLoadout = new KitLoadout();
	@Getter @Setter private KitGameRules gameRules = new KitGameRules();
	@Getter @Setter private List<KitMatchType> kitMatchTypes = new ArrayList<>();
	@Getter @Setter private List<KitExtraItem> kitExtraItems = new ArrayList<>();

	public Kit(String name) {
		this.name = name;
		this.displayName = name;
		this.priority = 0;
		this.displayIcon = new ItemStack(Material.DIAMOND_SWORD);
	}

	public static void init() {
		FileConfiguration fileConfig = Eden.INSTANCE.getKitFile().getConfiguration();
		ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");
		if (kitSection == null) {
			return;
		}

		kitSection.getKeys(false).forEach(id -> {
			boolean enabled = kitSection.getBoolean(id + ".enabled");
			boolean ranked = kitSection.getBoolean(id + ".ranked");
			String displayName = kitSection.getString(id + ".display-name");
			int priority = kitSection.getInt(id + ".priority");
			int damageTicks = kitSection.getInt(id + ".damage-ticks");
			ItemStack displayIcon = BukkitSerialization.itemStackFromBase64(kitSection.getString(id + ".display-icon"));
			List<String> description = Eden.GSON.fromJson(kitSection.getString(id + ".description"), GsonType.STRING_LIST);
			Collection<PotionEffect> effects = EffectSerialization.deserializeEffects(kitSection.getString(id + ".potion-effects"));
			ItemStack[] armor = BukkitSerialization.itemStackArrayFromBase64(kitSection.getString(id + ".armor-loadout"));
			ItemStack[] contents = BukkitSerialization.itemStackArrayFromBase64(kitSection.getString(id + ".contents-loadout"));
			KitGameRules gameRules = Eden.GSON.fromJson(kitSection.getString(id + ".game-rules"), GsonType.KIT_GAME_RULES);
			List<KitMatchType> kitMatchTypes = Eden.GSON.fromJson(kitSection.getString(id + ".kit-match-types"), GsonType.KIT_MATCH_TYPES);
			List<KitExtraItem> kitExtraItems = Eden.GSON.fromJson(kitSection.getString(id + ".kit-extra-item"), GsonType.KIT_EXTRA_ITEM);

			Kit kit = new Kit(id);
			kit.setEnabled(enabled);
			kit.setRanked(ranked);
			kit.setDisplayName(displayName);
			kit.setPriority(priority);
			kit.setDamageTicks(damageTicks);
			kit.setDisplayIcon(displayIcon);
			kit.setDescription(description);
			kit.setEffects(effects);
			kit.getKitLoadout().setArmor(armor);
			kit.getKitLoadout().setContents(contents);
			kit.setGameRules(gameRules);
			kit.setKitMatchTypes(kitMatchTypes);
			kit.setKitExtraItems(kitExtraItems);

			kits.add(kit);
		});

		sortKit();
	}

	public static void sortKit() {
		kits.sort(Comparator.comparing(Kit::getPriority));
	}

	public ItemStack getDisplayIcon() {
		return displayIcon.clone();
	}

	public void delete() {
		Kit.getKits().remove(this);
	}

	public void save() {
		FileConfiguration fileConfig = Eden.INSTANCE.getKitFile().getConfiguration();
		String kitRoot = "kits." + name;
		fileConfig.set(kitRoot, null); //Remove everything related to that kit first, then add the details one by one
		fileConfig.set(kitRoot + ".enabled", enabled);
		fileConfig.set(kitRoot + ".ranked", ranked);
		fileConfig.set(kitRoot + ".display-name", displayName);
		fileConfig.set(kitRoot + ".priority", priority);
		fileConfig.set(kitRoot + ".damage-ticks", damageTicks);
		fileConfig.set(kitRoot + ".display-icon", BukkitSerialization.itemStackToBase64(displayIcon));
		fileConfig.set(kitRoot + ".description", Eden.GSON.toJson(description, GsonType.STRING_LIST));
		fileConfig.set(kitRoot + ".potion-effects", EffectSerialization.serializeEffects(effects));
		fileConfig.set(kitRoot + ".armor-loadout", BukkitSerialization.itemStackArrayToBase64(kitLoadout.getArmor()));
		fileConfig.set(kitRoot + ".contents-loadout", BukkitSerialization.itemStackArrayToBase64(kitLoadout.getContents()));
		fileConfig.set(kitRoot + ".game-rules", Eden.GSON.toJson(gameRules, GsonType.KIT_GAME_RULES));
		fileConfig.set(kitRoot + ".kit-match-types", Eden.GSON.toJson(kitMatchTypes, GsonType.KIT_MATCH_TYPES));
		fileConfig.set(kitRoot + ".kit-extra-item", Eden.GSON.toJson(kitExtraItems, GsonType.KIT_EXTRA_ITEM));

		Eden.INSTANCE.getKitFile().save();
	}

	public void autoSave() {
		if (Config.ARENA_KIT_AUTO_SAVE.toBoolean()) {
			save();
		}
	}

	public static Kit getByName(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

}
