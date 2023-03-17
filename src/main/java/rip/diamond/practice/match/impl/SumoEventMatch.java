package rip.diamond.practice.match.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.events.impl.SumoEvent;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchType;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SumoEventMatch extends Match {

    private final SumoEvent event;

    public SumoEventMatch(SumoEvent event, ArenaDetail arena, Kit kit, List<Team> teams) {
        super(arena, kit, teams);
        this.event = event;

        setDuel(true);

        Common.debug("正在開始一場 SumoEventMatch 戰鬥 (" + teams.stream().map(team -> team.getLeader().getUsername()).collect(Collectors.joining(" vs ")) + ") (職業: " + kit.getName() + ") (地圖: " + arena.getArena().getName() + ") (UUID: " + getUuid() + ")");
    }

    @Override
    public void setupTeamSpawnLocation() {
        Location location = arenaDetail.getSpectator();
        for (Team team : getTeams()) {
            team.setSpawnLocation(location);
        }
    }

    @Override
    public void displayMatchEndMessages() {

    }

    @Override
    public void displayMatchEndTitle() {

    }

    @Override
    public void calculateMatchStats() {

    }

    @Override
    public MatchType getMatchType() {
        return MatchType.SUMO_EVENT;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        if (event.isFighting(team)) {
            return team == event.getTeamA() ? event.getTeamB() : event.getTeamA();
        }
        throw new PracticeUnexpectedException("Unsupported (" + getClass().getSimpleName() + ", getOpponentTeam(Team))");
    }

    @Override
    public TeamPlayer getOpponent(TeamPlayer teamPlayer) {
        throw new PracticeUnexpectedException("Unsupported (" + getClass().getSimpleName() + ", getOpponent(TeamPlayer))");
    }

    @Override
    public List<TeamPlayer> getWinningPlayers() {
        if (getState() != MatchState.ENDING) {
            throw new PracticeUnexpectedException("Cannot get Winning Players when match isn't ending");
        }
        return getWinningTeam().getTeamPlayers();
    }

    @Override
    public Team getWinningTeam() {
        List<Team> all = new ArrayList<>(getTeams());
        all.removeIf(Team::isEliminated);
        return all.get(0);
    }

    @Override
    public List<String> getMatchScoreboard(Player player) {
        return event.getInGameScoreboard(player);
    }

    @Override
    public List<String> getSpectateScoreboard(Player player) {
        return null;
    }
}
