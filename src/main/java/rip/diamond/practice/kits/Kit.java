package rip.diamond.practice.kits;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.GsonType;
import rip.diamond.practice.util.Tasks;
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
	@Getter private KitGameRules gameRules = new KitGameRules();
	@Getter private List<KitMatchType> kitMatchTypes = new ArrayList<>();
	@Getter private List<KitExtraItem> kitExtraItems = new ArrayList<>();

	public Kit(String name) {
		this.name = name;
		this.displayName = name;
		this.priority = 0;
		this.displayIcon = new ItemStack(Material.DIAMOND_SWORD);
	}

	public static void init() {
		Eden.INSTANCE.getMongoManager().getKits().find().into(new ArrayList<>()).forEach(document -> {
			Kit kit = new Kit(document.getString("_id"));
			kit.load(document);
			kits.add(kit);
		});
		kits.sort(Comparator.comparing(Kit::getPriority));
	}

	public static void sortKit() {
		kits.sort(Comparator.comparing(Kit::getPriority));
	}

	public ItemStack getDisplayIcon() {
		return displayIcon.clone();
	}

	public void load(Document document) {
		this.enabled = document.getBoolean("enabled");
		this.ranked = document.getBoolean("ranked");
		this.displayName = document.getString("display-name");
		this.priority = document.getInteger("priority");
		this.damageTicks = document.getInteger("damage-ticks");
		this.displayIcon = BukkitSerialization.itemStackFromBase64(document.getString("display-icon"));
		this.description = Eden.GSON.fromJson(document.getString("description"), GsonType.STRING_LIST);
		this.effects = EffectSerialization.deserializeEffects(document.getString("potion-effects"));
		this.kitLoadout.setArmor(BukkitSerialization.itemStackArrayFromBase64(document.getString("armor-loadout")));
		this.kitLoadout.setContents(BukkitSerialization.itemStackArrayFromBase64(document.getString("contents-loadout")));
		this.gameRules = Eden.GSON.fromJson(document.getString("game-rules"), GsonType.KIT_GAME_RULES);
		this.kitMatchTypes = Eden.GSON.fromJson(document.getString("kit-match-types"), GsonType.KIT_MATCH_TYPES);
		this.kitExtraItems = Eden.GSON.fromJson(document.getString("kit-extra-item"), GsonType.KIT_EXTRA_ITEM);
	}

	public void delete(boolean async) {
		if (async) {
			Tasks.runAsync(() -> delete(false));
			return;
		}
		Kit.getKits().remove(this);
		Eden.INSTANCE.getMongoManager().getKits().deleteMany(Filters.eq("_id", name));
	}

	public void save(boolean async) {
		if (async) {
			Tasks.runAsync(() -> save(false));
			return;
		}

		final Document document = Eden.INSTANCE.getMongoManager().getKits().find(Filters.eq("_id", name)).first();

		if (document == null) {
			Eden.INSTANCE.getMongoManager().getKits().insertOne(toBson());
			return;
		}

		Eden.INSTANCE.getMongoManager().getKits().replaceOne(document, toBson(), new ReplaceOptions().upsert(true));
	}

	public static Kit getByName(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

	public static void apply(Player player, Kit kit) {
		player.getInventory().setContents(kit.getKitLoadout().getContents());
		player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
	}

	public final Document toBson() {
		return new Document()
				.append("_id", name)
				.append("enabled", enabled)
				.append("ranked", ranked)
				.append("display-name", displayName)
				.append("priority", priority)
				.append("damage-ticks", damageTicks)
				.append("display-icon", BukkitSerialization.itemStackToBase64(displayIcon))
				.append("description", Eden.GSON.toJson(description, GsonType.STRING_LIST))
				.append("potion-effects", EffectSerialization.serializeEffects(effects))
				.append("armor-loadout", BukkitSerialization.itemStackArrayToBase64(kitLoadout.getArmor()))
				.append("contents-loadout", BukkitSerialization.itemStackArrayToBase64(kitLoadout.getContents()))
				.append("game-rules", Eden.GSON.toJson(gameRules, GsonType.KIT_GAME_RULES))
				.append("kit-match-types", Eden.GSON.toJson(kitMatchTypes, GsonType.KIT_MATCH_TYPES))
				.append("kit-extra-item", Eden.GSON.toJson(kitExtraItems, GsonType.KIT_EXTRA_ITEM))
				;
	}

}
