package rip.diamond.practice.match.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.HeadUtil;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SpectateTeleportMenu extends PaginatedMenu {

    private final Match match;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.MATCH_SPECTATE_TELEPORT_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        match.getTeamPlayers().stream().filter(TeamPlayer::isAlive).forEach(teamPlayer -> {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM)
                            .durability(3)
                            .headTexture(HeadUtil.getValue(teamPlayer.getPlayer()))
                            .name(Language.MATCH_SPECTATE_TELEPORT_MENU_BUTTON_NAME.toString(teamPlayer.getUsername()))
                            .lore(Language.MATCH_SPECTATE_TELEPORT_MENU_BUTTON_LORE.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    PlayerProfile profile = PlayerProfile.get(player);
                    if ((profile.getPlayerState() != PlayerState.IN_MATCH && profile.getPlayerState() != PlayerState.IN_SPECTATING) || profile.getMatch() == null) {
                        Language.MATCH_SPECTATE_TELEPORT_MENU_WRONG_STATE.sendMessage(player);
                        return;
                    }
                    if (!teamPlayer.isAlive()) {
                        Language.MATCH_SPECTATE_TELEPORT_MENU_ALREADY_DIED.sendMessage(player);
                        return;
                    }
                    Player tPlayer = teamPlayer.getPlayer();
                    if (tPlayer == null) {
                        Language.MATCH_SPECTATE_TELEPORT_MENU_NOT_ONLINE.sendMessage(player);
                        return;
                    }
                    PlayerProfile tProfile = PlayerProfile.get(tPlayer);
                    if (tProfile.getPlayerState() != PlayerState.IN_MATCH || tProfile.getMatch() == null) {
                        Language.MATCH_SPECTATE_TELEPORT_MENU_TARGET_WRONG_STATE.sendMessage(player);
                        return;
                    }
                    if (tProfile.getMatch() != profile.getMatch()) {
                        Language.MATCH_SPECTATE_TELEPORT_MENU_NOT_SAME_MATCH.sendMessage(player);
                        return;
                    }
                    Util.teleport(player, teamPlayer.getPlayer().getLocation());
                }
            });
        });

        return buttons;
    }
}
