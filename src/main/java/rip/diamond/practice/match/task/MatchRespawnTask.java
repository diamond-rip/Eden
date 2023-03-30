package rip.diamond.practice.match.task;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.match.team.TeamPlayer;
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
        if (teamPlayer == null || !teamPlayer.isRespawning() || teamPlayer.isDisconnected() || teamPlayer.getPlayer() == null || match.getState() == MatchState.STARTING || match.getState() == MatchState.ENDING) {
            cancel();
            return;
        }
        Player player = teamPlayer.getPlayer();

        if (getTicks() <= 0) {
            cancel();
            match.respawn(teamPlayer);
            return;
        }
        Common.sendMessage(player, Language.MATCH_RESPAWN_COUNTDOWN.toString(getTicks()));
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

    public void instantRespawn() {
        setTicks(0);
        onRun();
    }
}
