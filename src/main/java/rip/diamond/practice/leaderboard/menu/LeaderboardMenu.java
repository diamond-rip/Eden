package rip.diamond.practice.leaderboard.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.leaderboard.menu.impl.*;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LeaderboardMenu extends Menu {
    private final Eden plugin = Eden.INSTANCE;
    private final Integer[] ALLOWED_SLOT = new Integer[]{
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    @Override
    public int getSize() {
        return 9*6;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;

        for (Button button : getLeaderboardButtons(player)) {
            if (i >= ALLOWED_SLOT.length) {
                Language.LEADERBOARD_ERROR_NOT_ENOUGH_SLOT.sendMessage(player);
                continue;
            }
            buttons.put(ALLOWED_SLOT[i++], button);
        }

        buttons.put(getSize() - 9 + 1, new SwitchLeaderboardButton(Material.CARPET, 1, Language.LEADERBOARD_WINS_MENU_TITLE.toString(), WinsLeaderboardMenu.class));
        buttons.put(getSize() - 9 + 2, new SwitchLeaderboardButton(Material.CARPET, 2, Language.LEADERBOARD_ELO_MENU_TITLE.toString(), EloLeaderboardMenu.class));
        buttons.put(getSize() - 9 + 4, new SwitchLeaderboardButton(Material.DIAMOND, 0, Language.LEADERBOARD_SWITCH_LEADERBOARD_BUTTON_VIEW_STATS_BUTTON_NAME.toString(), KitStatsMenu.class));
        buttons.put(getSize() - 9 + 6, new SwitchLeaderboardButton(Material.CARPET, 3, Language.LEADERBOARD_WINSTREAK_MENU_TITLE.toString(), WinstreakLeaderboardMenu.class));
        buttons.put(getSize() - 9 + 7, new SwitchLeaderboardButton(Material.CARPET, 4, Language.LEADERBOARD_BEST_WINSTREAK_MENU_TITLE.toString(), BestWinstreakLeaderboardMenu.class));

        for (int j = 0; j < getSize(); j++) {
            if (!buttons.containsKey(j)) {
                buttons.put(j, placeholderButton);
            }
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class SwitchLeaderboardButton extends Button {
        private final Material material;
        private final int durability;
        private final String name;
        private final Class<?> clazz;
        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(material)
                    .durability(durability)
                    .name(CC.AQUA + name)
                    .lore(Language.LEADERBOARD_SWITCH_LEADERBOARD_BUTTON_VIEW_STATS_BUTTON_LORE.toStringList());
                    ;
            if (clazz.getName().equals(LeaderboardMenu.this.getClass().getName())) {
                builder.glow();
            }
            return builder.build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clazz.equals(KitStatsMenu.class)) {
                new KitStatsMenu(PlayerProfile.get(player)).openMenu(player);
                return;
            }
            try {
                LeaderboardMenu menu = (LeaderboardMenu) clazz.newInstance();
                menu.openMenu(player);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public abstract String getTitle(Player player);

    public abstract List<Button> getLeaderboardButtons(Player player);
}
