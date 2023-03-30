package rip.diamond.practice.leaderboard.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.leaderboard.menu.LeaderboardMenu;
import rip.diamond.practice.util.menu.Button;

import java.util.*;

public class BestWinstreakLeaderboardMenu extends LeaderboardMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.LEADERBOARD_BEST_WINSTREAK_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        getPlugin().getLeaderboardManager().getBestWinstreakLeaderboard().values().stream().sorted(Comparator.comparing(leaderboard -> leaderboard.getKit().getPriority())).forEach(leaderboard -> buttons.put(buttons.size(), new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return leaderboard.getDisplayIcon();
            }
        }));

        return buttons;
    }
}
