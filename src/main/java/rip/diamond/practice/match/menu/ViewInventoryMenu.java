package rip.diamond.practice.match.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.match.PostMatchInventory;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.*;

@AllArgsConstructor
public class ViewInventoryMenu extends Menu {

	private PostMatchInventory info;

	@Override
	public String getTitle(Player player) {
		return info.getOwner() + " 的物品欄";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int i = 0; i < info.getContents().length; i++) {
			ItemStack itemStack = InventoryUtil.fixInventoryOrder(info.getContents())[i];

			if (itemStack != null && itemStack.getType() != Material.AIR) {
				buttons.put(i, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return itemStack;
					}
				});
			}
		}

		for (int i = 36; i < 45; i++) {
			buttons.put(i, placeholderButton);
		}

		for (int i = 0; i < info.getArmor().length; i++) {
			ItemStack itemStack = info.getArmor()[i];
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				buttons.put(48-i, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return itemStack;
					}
				});
			}
		}

		buttons.put(49, new PlayerInformationButton());
		buttons.put(50, new EffectsButton());
		buttons.put(51, new HealingButton());
		buttons.put(52, new StatisticsButton());
		if (info.getSwitchToUUID() != null) {
			buttons.put(53, new SwitchInventoryButton());
		}

		return buttons;
	}

	@Override
	public void onOpen(Player player) {
		player.sendMessage(CC.WHITE + "你正在觀看 " + CC.AQUA + info.getOwner() + CC.WHITE + " 的物品欄");
	}

	private class SwitchInventoryButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.LEVER)
					.name(CC.YELLOW + "轉換至 " + info.getSwitchTo() + " 的物品欄")
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			player.performCommand("viewinv " + info.getSwitchToUUID().toString());
		}
	}

	private class PlayerInformationButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.SKULL_ITEM)
					.durability(3)
					.headTexture(info.getOwnerHeadValue())
					.name(CC.AQUA + "玩家資訊")
					.lore(
							"",
							CC.WHITE + "血量: " + CC.AQUA + info.getHealth() + "/" + info.getMaxHealth() + " " + CC.DARK_RED + Symbols.HEALTH,
							CC.WHITE + "飢餓度: " + CC.AQUA + info.getHunger() + "/20",
							""
					)
					.build();
		}
	}

	@AllArgsConstructor
	private class EffectsButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			ItemBuilder builder = new ItemBuilder(Material.POTION).name(CC.AQUA + "藥水效果");

			if (info.getEffects().isEmpty()) {
				builder.lore("", CC.WHITE + "沒有任何藥水效果", "");
			} else {
				List<String> lore = new ArrayList<>();
				info.getEffects().forEach(effect -> {
					String name = WordUtil.toCapital(effect.getType().getName().replace("_", " ")) + " " + (effect.getAmplifier() + 1);
					String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000L) + ")";
					lore.add(CC.YELLOW + name + duration);
				});
				lore.add(0, "");
				lore.add("");
				builder.lore(lore);
			}
			return builder.build();
		}
	}

	@AllArgsConstructor
	private class HealingButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(info.getHealingMethod() == null ? new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).build() : info.getHealingMethod().getItem().clone())
					.name(CC.AQUA + "治療物品資訊")
					.lore("", info.getHealingMethod() == null ? CC.RED + "未能找到任何治療物品" : CC.AQUA + info.getOwner() + CC.WHITE + " 還有 " + CC.AQUA + info.getRemainingHealing() + CC.WHITE + " 個" + info.getHealingMethod().getName(), "")
					.build();
		}
	}

	private class StatisticsButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.PAPER)
					.name("&b戰鬥統計")
					.lore(Arrays.asList(
							"",
							CC.WHITE + "擊中次數: " + CC.AQUA + info.getHits(),
							CC.WHITE + "最高連擊: " + CC.AQUA + info.getLongestCombo(),
							CC.WHITE + "拋擲藥水次數: " + CC.AQUA + info.getPotionsThrown(),
							CC.WHITE + "錯過的藥水: " + CC.AQUA + info.getPotionsMissed(),
							CC.WHITE + "拋擲藥水準確度: " + CC.AQUA + info.getPotionAccuracy() + "%",
							""
					))
					.build();
		}
	}

}