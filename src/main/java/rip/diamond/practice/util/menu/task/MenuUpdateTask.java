package rip.diamond.practice.util.menu.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.TaskTicker;
import rip.diamond.practice.util.menu.Menu;

import java.util.Map;
import java.util.UUID;

public class MenuUpdateTask extends TaskTicker {

	public MenuUpdateTask() {
		super(0, 20, false);
	}

	@Override
	public void onRun() {
		for (Map.Entry<UUID, Menu> menuMap : Menu.currentlyOpenedMenus.entrySet()) {
			UUID uuid = menuMap.getKey();
			Menu menu = menuMap.getValue();

			if (uuid == null || menu == null) {
				Menu.currentlyOpenedMenus.remove(uuid, menu);
				continue;
			}

			final Player player = Bukkit.getPlayer(uuid);
			if (player == null) return;
			if (menu.isAutoUpdate()) {
				menu.openMenu(player);
			}
		}
	}

	@Override
	public TickType getTickType() {
		return TickType.NONE;
	}

	@Override
	public int getStartTick() {
		return 0;
	}

}