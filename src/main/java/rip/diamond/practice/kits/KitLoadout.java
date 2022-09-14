package rip.diamond.practice.kits;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.event.KitLoadoutReceivedEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.serialization.BukkitSerialization;

public class KitLoadout {

	@Getter @Setter private String customName = "Default";
	private String armor;
	private String contents;

	public void fromBson(Document document) {
		customName = document.getString("customName");
		armor = document.getString("armor");
		contents = document.getString("contents");
	}

	public Document toBson() {
		return new Document()
				.append("customName", contents)
				.append("armor", armor)
				.append("contents", contents)
				;
	}

	public KitLoadout() {
		this.armor = BukkitSerialization.itemStackArrayToBase64(new ItemStack[4]);
		this.contents = BukkitSerialization.itemStackArrayToBase64(new ItemStack[36]);
	}

	public KitLoadout(String customName, Kit kit) {
		this.customName = customName;
		this.armor = BukkitSerialization.itemStackArrayToBase64(kit.getKitLoadout().getArmor());
		this.contents = BukkitSerialization.itemStackArrayToBase64(new ItemStack[36]);
	}

	public KitLoadout(String customName) {
		this.customName = customName;
		this.armor = BukkitSerialization.itemStackArrayToBase64(new ItemStack[4]);
		this.contents = BukkitSerialization.itemStackArrayToBase64(new ItemStack[36]);
	}

	public KitLoadout(String customName, String armor, String contents) {
		this.customName = customName;
		this.armor = armor;
		this.contents = contents;
	}

	public ItemStack[] getArmor() {
		return BukkitSerialization.itemStackArrayFromBase64(armor);
	}

	public ItemStack[] getContents() {
		return BukkitSerialization.itemStackArrayFromBase64(contents);
	}

	public String getArmorAsBase64() {
		return armor;
	}

	public String getContentsAsBase64() {
		return contents;
	}

	public void setArmor(ItemStack[] armor) {
		this.armor = BukkitSerialization.itemStackArrayToBase64(armor);
	}

	public void setContents(ItemStack[] contents) {
		this.contents = BukkitSerialization.itemStackArrayToBase64(contents);
	}

	public void apply(Match match, Player player) {
		player.getInventory().setArmorContents(null);
		player.getInventory().clear();

		player.getInventory().setArmorContents(getArmor());
		player.getInventory().setContents(getContents());

		match.getTeamPlayer(player).setKitLoadout(this);

		KitLoadoutReceivedEvent e = new KitLoadoutReceivedEvent(player, match, this);
		e.call();
	}
}
