package rip.diamond.practice.match.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchType;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.Clickable;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FFAMatch extends Match {
    public FFAMatch(ArenaDetail arena, Kit kit, List<Team> teams) {
        super(arena, kit, teams);

        setDuel(true);

        Common.debug("正在開始一場 FFAMatch 戰鬥 (" + teams.stream().map(team -> team.getLeader().getUsername()).collect(Collectors.joining(" vs ")) + ") (職業: " + kit.getName() + ") (地圖: " + arena.getArena().getName() + ") (UUID: " + getUuid() + ")");
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.FFA;
    }

    @Override
    public Team getOpponentTeam(Team team) {
        throw new PracticeUnexpectedException("Unsupported (" + getClass().getSimpleName() + ", getOpponentTeam(Team))");
    }

    @Override
    public TeamPlayer getOpponent(TeamPlayer teamPlayer) {
        throw new PracticeUnexpectedException("Unsupported (" + getClass().getSimpleName() + ", getOpponent(TeamPlayer))");
    }

    @Override
    public void setupTeamSpawnLocation() {
        Location location = getArenaDetail().getA();
        for (Team team : getTeams()) {
            team.setSpawnLocation(location);
        }
    }

    @Override
    public void displayMatchEndMessages() {
        Team winnerTeam = getWinningTeam();
        List<Team> loserTeams = new ArrayList<>(getTeams());
        loserTeams.removeIf(team -> team == winnerTeam);

        Clickable clickable1 = new Clickable(Language.MATCH_POST_MATCH_INVENTORY_WINNER.toString());
        Iterator<TeamPlayer> iterator1 = winnerTeam.getTeamPlayers().iterator();
        while (iterator1.hasNext()) {
            TeamPlayer teamPlayer = iterator1.next();
            clickable1.add(teamPlayer.getUsername(), Language.MATCH_POST_MATCH_INVENTORY_HOVER.toString(teamPlayer.getUsername()), "/viewinv " + teamPlayer.getUuid());
            if (iterator1.hasNext()) {
                clickable1.add(Language.MATCH_SEPARATE.toString());
            }
        }
        Clickable clickable2 = new Clickable(Language.MATCH_POST_MATCH_INVENTORY_LOSER.toString());
        List<TeamPlayer> loserTeamPlayers = new ArrayList<>();
        loserTeams.forEach(team -> loserTeamPlayers.addAll(team.getTeamPlayers()));
        Iterator<TeamPlayer> iterator2 = loserTeamPlayers.iterator();
        while (iterator2.hasNext()) {
            TeamPlayer teamPlayer = iterator2.next();
            clickable2.add(teamPlayer.getUsername(), Language.MATCH_POST_MATCH_INVENTORY_HOVER.toString(teamPlayer.getUsername()), "/viewinv " + teamPlayer.getUuid());
            if (iterator2.hasNext()) {
                clickable2.add(Language.MATCH_SEPARATE.toString());
            }
        }

        Language.MATCH_POST_MATCH_INVENTORY_MESSAGE.toStringList().forEach(s -> {
            if (s.contains("{post-match-inventories}")) {
                getPlayersAndSpectators().forEach(p -> {
                    clickable1.sendToPlayer(p);
                    clickable2.sendToPlayer(p);
                });
            } else {
                getPlayersAndSpectators().forEach(p -> Common.sendMessage(p, s));
            }
        });
    }

    @Override
    public void displayMatchEndTitle() {
        Team winnerTeam = getWinningTeam();
        List<Team> loserTeams = new ArrayList<>(getTeams());
        loserTeams.removeIf(team -> team == winnerTeam);

        String winnerTeamPlayers = winnerTeam.getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));

        winnerTeam.broadcastTitle(Language.MATCH_END_TITLE_WIN_TITLE.toString(), Language.MATCH_END_TITLE_WIN_SUBTITLE.toString(winnerTeamPlayers));
        loserTeams.forEach(team -> team.broadcastTitle(Language.MATCH_END_TITLE_LOSE_TITLE.toString(), Language.MATCH_END_TITLE_LOSE_SUBTITLE.toString(winnerTeamPlayers)));
    }

    @Override
    public void calculateMatchStats() {

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
        if (all.isEmpty()) {
            return null;
        }
        return all.get(0);
    }

    @Override
    public List<String> getMatchScoreboard(Player player) {
        List<String> elements = new ArrayList<>();

        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_MATCH_FFA_ENDING.toStringList(player));
        } else {
            elements.addAll(Language.SCOREBOARD_IN_MATCH_FFA_FIGHTING.toStringList(player));
        }
        return elements;
    }

    @Override
    public List<String> getSpectateScoreboard(Player player) {
        List<String> elements = new ArrayList<>();

        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_SPECTATE_FFA_ENDING.toStringList(player));
        } else {
            elements.addAll(Language.SCOREBOARD_IN_SPECTATE_FFA_FIGHTING.toStringList(player));
        }
        return elements;
    }
}
