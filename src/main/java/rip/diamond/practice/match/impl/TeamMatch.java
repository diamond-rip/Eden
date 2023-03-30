package rip.diamond.practice.match.impl;

import lombok.Getter;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class TeamMatch extends Match {
    @Getter private final Team teamA;
    @Getter private final Team teamB;

    public TeamMatch(ArenaDetail arena, Kit kit, Team teamA, Team teamB) {
        super(arena, kit, Arrays.asList(teamA, teamB));
        this.teamA = teamA;
        this.teamB = teamB;

        setDuel(true);

        Common.debug("正在開始一場 TeamMatch 戰鬥 (" + teamA.getLeader().getUsername() + " vs " + teamB.getLeader().getUsername() + ") (職業: " + kit.getName() + ") (地圖: " + arena.getArena().getName() + ") (UUID: " + getUuid() + ")");
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.SPLIT;
    }

    @Override
    public void setupTeamSpawnLocation() {
        Location teamALocation = getArenaDetail().getA();
        Location teamBLocation = getArenaDetail().getB();

        teamA.setSpawnLocation(teamALocation);
        teamB.setSpawnLocation(teamBLocation);
    }

    @Override
    public void displayMatchEndMessages() {
        Team winnerTeam = getWinningTeam();
        Team loserTeam = getOpponentTeam(getWinningTeam());

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
        Iterator<TeamPlayer> iterator2 = loserTeam.getTeamPlayers().iterator();
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
        Team loserTeam = getOpponentTeam(getWinningTeam());

        String winnerTeamPlayers = winnerTeam.getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));

        winnerTeam.broadcastTitle(Language.MATCH_END_TITLE_WIN_TITLE.toString(), Language.MATCH_END_TITLE_WIN_SUBTITLE.toString(winnerTeamPlayers));
        loserTeam.broadcastTitle(Language.MATCH_END_TITLE_LOSE_TITLE.toString(), Language.MATCH_END_TITLE_LOSE_SUBTITLE.toString(winnerTeamPlayers));
    }

    @Override
    public void calculateMatchStats() {

    }

    @Override
    public List<TeamPlayer> getWinningPlayers() {
        if (getState() != MatchState.ENDING) {
            throw new PracticeUnexpectedException("Cannot get Winning Players when match isn't ending");
        }
        return getWinningTeam().getTeamPlayers().stream().filter(TeamPlayer::isAlive).collect(Collectors.toList());
    }

    @Override
    public Team getWinningTeam() {
        if (teamA.isEliminated()) {
            return teamB;
        } else if (teamB.isEliminated()) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getMatchScoreboard(Player player) {
        List<String> elements = new ArrayList<>();

        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_MATCH_TEAMS_ENDING.toStringList(player));
        } else {
            if (getKit().getGameRules().isBoxing()) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_TEAMS_BOXING.toStringList(player));
            } else if (getKit().getGameRules().isBed()) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_TEAMS_BED.toStringList(player));
            } else if (getKit().getGameRules().isPoint(this)) {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_TEAMS_POINT.toStringList(player));
            } else {
                elements.addAll(Language.SCOREBOARD_IN_MATCH_TEAMS_FIGHTING.toStringList(player));
            }
        }
        return elements;
    }

    @Override
    public List<String> getSpectateScoreboard(Player player) {
        List<String> elements = new ArrayList<>();

        if (getState() == MatchState.ENDING) {
            elements.addAll(Language.SCOREBOARD_IN_SPECTATE_TEAMS_ENDING.toStringList(player));
        } else {
            if (getKit().getGameRules().isBoxing()) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_TEAMS_BOXING.toStringList(player));
            } else if (getKit().getGameRules().isBed()) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_TEAMS_BED.toStringList(player));
            } else if (getKit().getGameRules().isPoint(this)) {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_TEAMS_POINT.toStringList(player));
            } else {
                elements.addAll(Language.SCOREBOARD_IN_SPECTATE_TEAMS_FIGHTING.toStringList(player));
            }
        }
        return elements;
    }

    public Team getOpponentTeam(Team team) {
        if (teamA.equals(team)) {
            return teamB;
        } else if (teamB.equals(team)) {
            return teamA;
        } else {
            return null;
        }
    }

    @Override
    public TeamPlayer getOpponent(TeamPlayer teamPlayer) {
        throw new PracticeUnexpectedException("Unsupported (TeamMatch, getOpponent(TeamPlayer))");
    }

    public Team getOpponentTeam(Player player) {
        if (teamA.containsPlayer(player)) {
            return teamB;
        } else if (teamB.containsPlayer(player)) {
            return teamA;
        } else {
            return null;
        }
    }
}
