package rip.diamond.practice.match.task;

import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.CenteredMessageSender;

import java.util.stream.Collectors;

public class MatchNewRoundTask extends MatchTaskTicker {

    private final Match match;
    private final TeamPlayer scoredPlayer;
    private final boolean newRound;

    public MatchNewRoundTask(Match match, TeamPlayer scoredPlayer, boolean newRound) {
        super(0, 20, false, match);
        this.match = match;
        this.scoredPlayer = scoredPlayer;
        this.newRound = newRound;
    }

    @Override
    public void onRun() {
        if (match.getState() == MatchState.ENDING) {
            cancel();
            return;
        }

        if (getTicks() == 0) {
            match.broadcastMessage(Language.MATCH_NEW_ROUND_START_MESSAGE.toString());
            match.broadcastTitle(Language.MATCH_NEW_ROUND_START_TITLE.toString());
            match.broadcastSubTitle("");
            match.setState(MatchState.FIGHTING);
            match.broadcastSound(Sound.FIREWORK_BLAST);
            cancel();
            return;
        }

        match.broadcastMessage(CC.YELLOW + getTicks() + "...");
        match.broadcastTitle(CC.YELLOW + getTicks());
        match.broadcastSound(Sound.CLICK);
    }

    @Override
    public void preRun() {
        //To prevent any duplicate MatchNewRoundTask happens
        //This will occur when the match is sumo, and player walked into the water without taking any hits from opponent
        if (match.getTasks().stream().filter(taskTicker -> taskTicker != this).anyMatch(taskTicker -> taskTicker instanceof MatchNewRoundTask)) {
            cancel();
            return;
        }

        //Meaning the game is started and someone scored
        if (scoredPlayer != null && match.getState() != MatchState.STARTING && match.getKit().getGameRules().isPoint(match)) {
            Team team = match.getTeam(scoredPlayer);
            Player player = scoredPlayer.getPlayer();

            //Display scored message
            match.broadcastMessage(Language.MATCH_NEW_ROUND_START_SCORE.toStringList(
                    team.getTeamColor().getColor(),
                    scoredPlayer.getUsername(),
                    Eden.DECIMAL.format((player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts())),
                    team.getPoints(),
                    match.getOpponentTeam(team).getTeamColor().getColor(),
                    match.getOpponentTeam(team).getPoints()
            ).stream().map(CenteredMessageSender::getCenteredMessage).collect(Collectors.toList()));
        }

        if (newRound) {
            match.setState(MatchState.STARTING);

            //Teleport players into their team spawn
            match.getTeams().forEach(t -> t.teleport(t.getSpawnLocation()));
            match.getTeamPlayers().forEach(teamPlayer -> {
                if (teamPlayer.isRespawning()) {
                    //To prevent MatchRespawnTask gets cancelled because of a new round, we need to fully respawn the player instead of just giving the kit loadout. A fix for https://github.com/RealGoodestEnglish/Eden/issues/4
                    match.respawn(teamPlayer);
                } else {
                    teamPlayer.respawn(match);
                }
            });

            if (match.getKit().getGameRules().isResetArenaWhenGetPoint()) {
                match.getArenaDetail().restoreSnapshot();

                //Cancel any runnable which affects the gameplay
                match.getTasks().stream().filter(taskTicker -> taskTicker instanceof MatchClearBlockTask).forEach(BukkitRunnable::cancel);
            }
        }
    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        if (!newRound) {
            return 5;
        }
        return match.getKit().getGameRules().getNewRoundTime();
    }
}
