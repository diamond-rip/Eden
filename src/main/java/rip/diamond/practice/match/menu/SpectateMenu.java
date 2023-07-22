package rip.diamond.practice.match.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchType;
import rip.diamond.practice.match.impl.SoloMatch;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.TimeUtil;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SpectateMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.MATCH_SPECTATE_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        for (Match match : Match.getMatches().values()) {
            try {
                String title;
                if (match.getMatchType() == MatchType.SOLO) {
                    title = Language.MATCH_SPECTATE_MENU_BUTTON_NAME_SOLO.toString(((SoloMatch) match).getPlayerA().getUsername(), ((SoloMatch) match).getPlayerB().getUsername());
                } else if (match.getMatchType() == MatchType.SPLIT) {
                    title = Language.MATCH_SPECTATE_MENU_BUTTON_NAME_SPLIT.toString(((TeamMatch) match).getTeamA().getLeader().getUsername(), ((TeamMatch) match).getTeamB().getLeader().getUsername());
                } else if (match.getMatchType() == MatchType.FFA) {
                    title = Language.MATCH_SPECTATE_MENU_BUTTON_NAME_FFA.toString(match.getTeamPlayers().size());
                } else if (match.getMatchType() == MatchType.SUMO_EVENT && EdenEvent.getOnGoingEvent() != null) {
                    title = Language.MATCH_SPECTATE_MENU_BUTTON_NAME_SUMO_EVENT.toString();
                } else {
                    title = Language.MATCH_SPECTATE_MENU_BUTTON_NAME_ERROR.toString();
                }

                buttons.put(buttons.size(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(match.getKit().getDisplayIcon().clone())
                                .name(CC.AQUA + title)
                                .lore(Language.MATCH_SPECTATE_MENU_BUTTON_LORE.toStringList(player,
                                        TimeUtil.millisToTimer(match.getElapsedDuration()),
                                        match.getKit().getDisplayName(),
                                        match.getArenaDetail().getArena().getDisplayName(),
                                        match.getQueueType().getReadable(),
                                        match.getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(CC.GRAY + ", " + CC.AQUA)),
                                        match.getSpectators().size()
                                ))
                                .build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        player.closeInventory();
                        match.joinSpectate(player, match.getMatchPlayers().get(0));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                buttons.put(buttons.size(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(Material.BEDROCK)
                                .name(Language.MATCH_SPECTATE_MENU_ERROR_BUTTON_NAME.toString())
                                .lore(Language.MATCH_SPECTATE_MENU_ERROR_BUTTON_LORE.toStringList(player, match.getUuid().toString()))
                                .build();
                    }
                });
            }
        }
        return buttons;
    }
}
