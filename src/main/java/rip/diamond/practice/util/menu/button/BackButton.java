package rip.diamond.practice.util.menu.button;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

@RequiredArgsConstructor
@AllArgsConstructor
public class BackButton extends Button {

	private final Material material;
	private int durability = 0;
	private final Menu back;

	@Override
	public ItemStack getButtonItem(Player player) {
		return new ItemBuilder(material)
				.name(Language.BUTTON_BACK_NAME.toString())
				.durability(durability)
				.lore(Language.BUTTON_BACK_LORE.toStringList(player))
				.build();
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		Button.playNeutral(player);
		back.openMenu(player);
	}

}
