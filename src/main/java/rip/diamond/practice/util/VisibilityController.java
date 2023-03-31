package rip.diamond.practice.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;

//Credit: https://github.com/DevDrizzy/PotPvPReprised/blob/master/src/main/java/net/frozenorb/potpvp/util/VisibilityUtils.java
//I am too lazy to use all my braincells to code a visibility controller

@UtilityClass
public class VisibilityController {

    public void updateVisibility(Player player) {
        Tasks.run(() -> {
            for (Player target : Util.getOnlinePlayers()) {
                if (shouldSeePlayer(target, player)) {
                    target.showPlayer(player);
                } else {
                    target.hidePlayer(player);
                }

                if (shouldSeePlayer(player, target)) {
                    player.showPlayer(target);
                } else {
                    player.hidePlayer(target);
                }
            }
        });
    }

    private boolean shouldSeePlayer(Player viewer, Player target) {
        if (viewer == null || target == null) {
            return false;
        }

        if (viewer == target) {
            return true;
        }

        PlayerProfile pViewer = PlayerProfile.get(viewer);
        PlayerProfile pTarget = PlayerProfile.get(target);

        if (pViewer == null || pTarget == null || pViewer.getPlayerState() == PlayerState.LOADING || pTarget.getPlayerState() == PlayerState.LOADING) {
            return false;
        }


        Match targetMatch = pTarget.getMatch();

        if (targetMatch == null) {
            //We're not in a match, so we hide other players based on their party/match
            Party targetParty = Party.getByPlayer(target);

            boolean configSettings = Config.LOBBY_DISPLAY_PLAYERS.toBoolean();
            boolean viewerPlayingMatch = pViewer.getPlayerState() == PlayerState.IN_MATCH && pViewer.getMatch() != null;
            boolean viewerSameParty = targetParty != null && targetParty.getMember(viewer.getUniqueId()) != null;

            return configSettings || viewerPlayingMatch || viewerSameParty;
        } else {
            //We're in a match, so we only hide other spectators (if our settings say so)
            boolean targetIsSpectator = targetMatch.getSpectators().contains(target) || !targetMatch.getTeamPlayer(target).isAlive() || targetMatch.getTeamPlayer(target).isRespawning();
            boolean viewerSpectateSetting = pViewer.getSettings().get(ProfileSettings.SPECTATOR_VISIBILITY).isEnabled();
            boolean viewerIsSpectator = pViewer.getPlayerState() == PlayerState.IN_SPECTATING && pViewer.getMatch() != null;
            //Also check if the match is the same or not
            boolean viewerMatchIsSame = targetMatch == pViewer.getMatch();

            return (!targetIsSpectator || (viewerSpectateSetting && viewerIsSpectator)) && viewerMatchIsSame;
        }
    }

}
