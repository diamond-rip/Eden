package rip.diamond.practice.match.menu;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
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
		return Language.MATCH_VIEW_INVENTORY_MENU_TITLE.toString(info.getOwner());
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
		Language.MATCH_VIEW_INVENTORY_MENU_OPEN_MESSAGE.sendMessage(player, info.getOwner());
	}

	private class SwitchInventoryButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.LEVER)
					.name(Language.MATCH_VIEW_INVENTORY_MENU_SWITCH_INVENTORY_BUTTON_NAME.toString(info.getSwitchTo()))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Util.performCommand(player, "viewinv " + info.getSwitchToUUID().toString());
		}
	}

	private class PlayerInformationButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.SKULL_ITEM)
					.durability(3)
					.headTexture(info.getOwnerHeadValue())
					.name(Language.MATCH_VIEW_INVENTORY_MENU_PLAYER_INFORMATION_BUTTON_NAME.toString())
					.lore(Language.MATCH_VIEW_INVENTORY_MENU_PLAYER_INFORMATION_BUTTON_LORE.toStringList(player, info.getHealth(), info.getMaxHealth(), info.getHunger()))
					.build();
		}
	}

	@AllArgsConstructor
	private class EffectsButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			ItemBuilder builder = new ItemBuilder(Material.POTION).name(Language.MATCH_VIEW_INVENTORY_MENU_EFFECTS_BUTTON_NAME.toString());

			if (info.getEffects().isEmpty()) {
				builder.lore(Language.MATCH_VIEW_INVENTORY_MENU_EFFECTS_BUTTON_NO_EFFECTS_LORE.toStringList(player));
			} else {
				List<String> lore = new ArrayList<>();
				info.getEffects().forEach(effect -> {
					String name = WordUtil.formatWords(effect.getType().getName()) + " " + (effect.getAmplifier() + 1);
					String duration = TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000L);
					lore.add(Language.MATCH_VIEW_INVENTORY_MENU_EFFECTS_BUTTON_EFFECTS_FORMAT.toString(name, duration));
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
					.name(Language.MATCH_VIEW_INVENTORY_MENU_HEALING_BUTTON_NAME.toString())
					.lore(info.getHealingMethod() == null ? Language.MATCH_VIEW_INVENTORY_MENU_HEALING_BUTTON_NO_HEALING_LORE.toStringList(player) : Language.MATCH_VIEW_INVENTORY_MENU_HEALING_BUTTON_HEALING_LORE.toStringList(info.getOwner(), HealingMethod.getHealingLeft(info.getHealingMethod(), info.getContents()), info.getHealingMethod().getName()))
					.build();
		}
	}

	private class StatisticsButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.PAPER)
					.name(Language.MATCH_VIEW_INVENTORY_MENU_STATISTICS_BUTTON_NAME.toString())
					.lore(Language.MATCH_VIEW_INVENTORY_MENU_STATISTICS_BUTTON_LORE.toStringList(player, info.getHits(), info.getBlockedHits(), info.getLongestCombo(), info.getPotionsThrown(), info.getPotionsMissed(), info.getPotionAccuracy()))
					.build();
		}
	}

}