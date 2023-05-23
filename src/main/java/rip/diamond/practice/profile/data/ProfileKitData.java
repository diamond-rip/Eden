package rip.diamond.practice.profile.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ProfileKitData {

	@Getter private int elo = Config.PROFILE_DEFAULT_ELO.toInteger();
	@Getter @Setter private int peakElo = Config.PROFILE_DEFAULT_ELO.toInteger();
	@Getter @Setter private int unrankedWon = 0;
	@Getter @Setter private int unrankedLost = 0;
	@Getter @Setter private int rankedWon = 0;
	@Getter @Setter private int rankedLost = 0;
	@Getter @Setter private int bestWinstreak = 0;
	@Getter @Setter private int winstreak = 0;
	@Getter @Setter private KitLoadout[] loadouts = new KitLoadout[4];

	public void fromBson(Document document) {
		elo = document.getInteger("elo");
		peakElo = document.getInteger("peakElo");
		unrankedWon = document.getInteger("unrankedWon");
		unrankedLost = document.getInteger("unrankedLost");
		rankedWon = document.getInteger("rankedWon");
		rankedLost = document.getInteger("rankedLost");
		bestWinstreak = document.getInteger("bestWinstreak");
		winstreak = document.getInteger("winstreak");
		loadouts = Eden.GSON.fromJson(document.getString("loadouts"), KitLoadout[].class);
	}

	public Document toBson() {
		return new Document()
				.append("elo", elo)
				.append("peakElo", peakElo)
				.append("unrankedWon", unrankedWon)
				.append("unrankedLost", unrankedLost)
				.append("rankedWon", rankedWon)
				.append("rankedLost", rankedLost)
				.append("won", getWon()) //Used for leaderboard display
				.append("bestWinstreak", bestWinstreak)
				.append("winstreak", winstreak)
				.append("loadouts", Eden.GSON.toJson(loadouts))
				;
	}

	public int getWon() {
		return unrankedWon + rankedWon;
	}

	public void incrementWon(boolean ranked) {
		if (ranked) {
			this.rankedWon++;
		} else {
			this.unrankedWon++;
		}
	}

	public void incrementLost(boolean ranked) {
		if (ranked) {
			this.rankedLost++;
		} else {
			this.unrankedLost++;
		}
	}

	public void setElo(int elo) {
		this.elo = elo;
		if (peakElo < elo) {
			peakElo = elo;
		}
	}

	public void calculateWinstreak(boolean won) {
		if (won) {
			winstreak++;
			if (bestWinstreak < winstreak) bestWinstreak = winstreak;
		} else {
			winstreak = 0;
		}
	}

	public KitLoadout getLoadout(int index) {
		return loadouts[index];
	}

	public void replaceKit(int index, KitLoadout loadout) {
		loadouts[index] = loadout;
	}

	public void deleteKit(int index) {
		loadouts[index] = null;
	}

	public List<ItemStack> getKitItems(Kit kit) {
		List<ItemStack> toReturn = new ArrayList<>();

		for (KitLoadout loadout : loadouts) {
			if (loadout != null) {
				ItemStack itemStack = new ItemBuilder(Material.ENCHANTED_BOOK)
						.name(CC.AQUA + loadout.getCustomName())
						.lore(Language.PROFILE_KIT_RIGHT_CLICK_TO_RECEIVE.toStringList())
						.build();

				net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
				NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
				compound.set("armor", new NBTTagString(loadout.getArmorAsBase64()));
				compound.set("contents", new NBTTagString(loadout.getContentsAsBase64()));
				compound.set("name", new NBTTagString(loadout.getCustomName()));
				toReturn.add(CraftItemStack.asBukkitCopy(nmsItem));
			}
		}

		ItemStack itemStack = new ItemBuilder(Material.BOOK)
				.name(CC.AQUA + kit.getKitLoadout().getCustomName())
				.lore(Language.PROFILE_KIT_RIGHT_CLICK_TO_RECEIVE.toStringList())
				.build();
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
		compound.set("armor", new NBTTagString(kit.getKitLoadout().getArmorAsBase64()));
		compound.set("contents", new NBTTagString(kit.getKitLoadout().getContentsAsBase64()));
		compound.set("name", new NBTTagString(kit.getKitLoadout().getCustomName()));

		toReturn.add(CraftItemStack.asBukkitCopy(nmsItem));

		return toReturn;
	}

}
