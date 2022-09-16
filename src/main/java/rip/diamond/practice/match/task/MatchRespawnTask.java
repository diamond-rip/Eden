package rip.diamond.practice.match.task;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.diamond.practice.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.VisibilityController;

public class MatchRespawnTask extends MatchTaskTicker {
    private final Match match;
    private final TeamPlayer teamPlayer;

    public MatchRespawnTask(Match match, TeamPlayer teamPlayer) {
        super(0, 20, false, match);
        this.match = match;
        this.teamPlayer = teamPlayer;
    }

    @Override
    public void onRun() {
        if (teamPlayer == null || teamPlayer.isDisconnected() || teamPlayer.getPlayer() == null || match.getState() == MatchState.STARTING || match.getState() == MatchState.ENDING) {
            cancel();
            return;
        }
        Player player = teamPlayer.getPlayer();

        if (getTicks() <= 0) {
            cancel();

            Team team = match.getTeam(player);
            team.getSpawnLocation().clone().add(0,0,0).getBlock().setType(Material.AIR);
            team.getSpawnLocation().clone().add(0,1,0).getBlock().setType(Material.AIR);
            Util.teleport(player, team.getSpawnLocation());
            player.setAllowFlight(false);
            player.setFlying(false);
            teamPlayer.setRespawning(false);
            match.getMatchPlayers().forEach(VisibilityController::updateVisibility);

            PlayerProfile profile = PlayerProfile.get(player);
            //So arrow will not be duplicated if GiveBackArrow is on
            if (profile.getCooldowns().get("arrow") != null) {
                profile.getCooldowns().get("arrow").cancelCountdown();
            }

            player.setExp(0);
            player.setLevel(0);

            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            teamPlayer.setProtectionUntil(System.currentTimeMillis() + (3*1000));
            teamPlayer.respawn(match);
            Language.MATCH_RESPAWN_MESSAGE.sendMessage(player);
            return;
        }
        Common.sendMessage(player, CC.YELLOW + getTicks() + "...");
    }

    @Override
    public void preRun() {
        Player player = teamPlayer.getPlayer();

        match.displayDeathMessage(teamPlayer, teamPlayer.getPlayer());
        teamPlayer.setRespawning(true);
        match.getMatchPlayers().forEach(VisibilityController::updateVisibility);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setAllowFlight(true);
        player.setFlying(true);
        if (getStartTick() > 0) Util.teleport(player, match.getTeam(player).getSpawnLocation());

        //我也不知道為什麼, 這兩項東西需要重新用一次才能正常運作
        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().clear();
    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        return match.getKit().getGameRules().getRespawnTime();
    }
}
