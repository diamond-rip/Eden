package rip.diamond.practice.leaderboard.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Language;
import rip.diamond.practice.leaderboard.menu.LeaderboardMenu;
import rip.diamond.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WinstreakLeaderboardMenu extends LeaderboardMenu {

    @Override
    public String getTitle(Player player) {
        return Language.LEADERBOARD_WINSTREAK_MENU_TITLE.toString();
    }

    @Override
    public List<Button> getLeaderboardButtons(Player player) {
        final List<Button> buttons = new ArrayList<>();
        getPlugin().getLeaderboardManager().getWinstreakLeaderboard().values().stream().sorted(Comparator.comparing(leaderboard -> leaderboard.getKit().getPriority())).forEach(leaderboard -> buttons.add(new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return leaderboard.getDisplayIcon();
            }
        }));
        return buttons;
    }
}
