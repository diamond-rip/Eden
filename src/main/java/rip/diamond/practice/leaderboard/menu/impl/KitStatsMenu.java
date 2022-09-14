package rip.diamond.practice.leaderboard.menu.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.leaderboard.menu.LeaderboardMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class KitStatsMenu extends LeaderboardMenu {

    private final PlayerProfile profile;

    @Override
    public String getTitle(Player player) {
        return Language.LEADERBOARD_KIT_STATS_MENU_TITLE.toString(profile.getUsername());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = super.getButtons(player);
        buttons.put(4, new GlobalStatsButton());
        return buttons;
    }

    @Override
    public List<Button> getLeaderboardButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (String kitName : profile.getKitData().keySet()) {
            buttons.add(new KitStatsButton(kitName));
        }
        buttons.sort(Comparator.comparing(slot -> {
            Kit kit = Kit.getByName(((KitStatsButton) slot).getKitName());
            if (kit != null) {
                return kit.getPriority();
            }
            return 0;
        }));
        return buttons;
    }

    private class GlobalStatsButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {

            int rankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedWon).sum();
            int rankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedLost).sum();
            int unrankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedWon).sum();
            int unrankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedLost).sum();

            return new ItemBuilder(Material.NETHER_STAR)
                    .name(Language.LEADERBOARD_KIT_STATS_MENU_GLOBAL_STATS_NAME.toString())
                    .lore(Language.LEADERBOARD_KIT_STATS_MENU_GLOBAL_STATS_LORE.toStringList(
                            unrankedWon,
                            unrankedLost,
                            Eden.DECIMAL.format((double) unrankedWon / (double) (unrankedLost == 0 ? 1 : unrankedLost)),
                            (profile.getKitData().values().stream().mapToInt(ProfileKitData::getElo).sum() / (profile.getKitData().size() == 0 ? 1 : profile.getKitData().size())),
                            rankedWon,
                            rankedLost,
                            Eden.DECIMAL.format((double) rankedWon / (double) (rankedLost == 0 ? 1 : rankedLost))
                    ))
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    private class KitStatsButton extends Button {
        private final String kitName;
        @Override
        public ItemStack getButtonItem(Player player) {
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return new ItemStack(Material.AIR);
            }
            int rankedWon = profile.getKitData().get(kitName).getRankedWon();
            int rankedLost = profile.getKitData().get(kitName).getRankedLost();
            int unrankedWon = profile.getKitData().get(kitName).getUnrankedWon();
            int unrankedLost = profile.getKitData().get(kitName).getUnrankedLost();
            int winstreak = profile.getKitData().get(kitName).getWinstreak();
            int bestWinstreak = profile.getKitData().get(kitName).getBestWinstreak();
            return new ItemBuilder(kit.getDisplayIcon().clone())
                    .name(Language.LEADERBOARD_KIT_STATS_MENU_KIT_STATS_NAME.toString(kit.getDisplayName()))
                    .lore(Language.LEADERBOARD_KIT_STATS_MENU_KIT_STATS_LORE.toStringList(
                            unrankedWon,
                            unrankedLost,
                            winstreak,
                            bestWinstreak,
                            Eden.DECIMAL.format((double) unrankedWon / (double) (unrankedLost == 0 ? 1 : unrankedLost)),
                            profile.getKitData().get(kitName).getElo(),
                            profile.getKitData().get(kitName).getPeakElo(),
                            rankedWon,
                            rankedLost,
                            Eden.DECIMAL.format((double) rankedWon / (double) (rankedLost == 0 ? 1 : rankedLost))
                    ))
                    .build();
        }
    }
}
