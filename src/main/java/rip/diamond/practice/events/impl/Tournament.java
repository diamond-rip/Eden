package rip.diamond.practice.events.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.EdenPlaceholder;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.event.MatchEndEvent;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventCountdown;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.impl.SoloMatch;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.Tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Tournament extends EdenEvent {

    private final Kit kit;
    private final List<Match> matches = new ArrayList<>();
    private TournamentState tournamentState = TournamentState.NONE;
    private int round = 0;

    public Tournament(String hoster, int minPlayers, int maxPlayers, Kit kit, int teamSize) {
        super(hoster, EventType.TOURNAMENT, minPlayers, maxPlayers, teamSize);

        this.kit = kit;
    }

    @Override
    public String getEventName() {
        return getTeamSize() + "v" + getTeamSize() + " " + getKit().getDisplayName() + " " + getEventType().getName();
    }

    private boolean canEnd() {
        return getState() == EventState.RUNNING && getParties().size() <= 1;
    }

    @Override
    public Listener constructListener() {
        return new Listener() {
            @EventHandler
            public void onMatchEnd(MatchEndEvent event) {
                Match match = event.getMatch();
                if (matches.contains(match)) {
                    matches.remove(match);

                    Team winner = match.getWinningTeam();
                    Team loser = match.getOpponentTeam(winner);

                    broadcast(Language.EVENT_TOURNAMENT_MATCH_END_MESSAGE.toString(getTeamName(winner), getTeamName(loser), matches.size()));

                    Party party = Party.getByPlayer(loser.getLeader().getPlayer());
                    //如果玩家在戰鬥時退出伺服器的話, Party 可能會是null
                    if (party != null) {
                        eliminate(party);
                    }

                    if (canEnd()) {
                        end(false);
                        return;
                    }

                    if (matches.isEmpty()) {
                        startNewRound();
                    }
                }
            }

            @EventHandler
            public void onDisband(PartyDisbandEvent event) {
                if (canEnd()) {
                    end(false);
                }
            }
        };
    }

    @Override
    public List<String> getLobbyScoreboard(Player player) {
        /*
         * 如果 tournamentState == TournamentState.NONE, 意思就是錦標賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (tournamentState == TournamentState.NONE) {
            return Language.EVENT_TOURNAMENT_SCOREBOARD_STARTING_EVENT.toStringList(player);
        }
        /*
         * 如果 tournamentState == TournamentState.STARTING_NEW_ROUND, 意思就是錦標賽正在準備開始新的一個回合
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        if (tournamentState == TournamentState.STARTING_NEW_ROUND) {
            return Language.EVENT_TOURNAMENT_SCOREBOARD_STARTING_NEW_ROUND.toStringList(player, round);
        }
        /*
         * 如果 tournamentState == TournamentState.FIGHTING, 意思就是錦標賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        if (tournamentState == TournamentState.FIGHTING) {
            return Language.EVENT_TOURNAMENT_SCOREBOARD_FIGHTING.toStringList(player, round, matches.size());
        }
        return ImmutableList.of(EdenPlaceholder.SKIP_LINE);
    }

    @Override
    public List<String> getInGameScoreboard(Player player) {
        return null;
    }

    @Override
    public List<String> getStatus(Player player) {
        /*
         * 如果 tournamentState == TournamentState.NONE, 意思就是錦標賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (tournamentState == TournamentState.NONE) {
            return Language.EVENT_SUMO_EVENT_STATUS_STARTING_EVENT.toStringList(player, getUncoloredEventName());
        }
        /*
         * 如果 tournamentState == TournamentState.STARTING_NEW_ROUND, 意思就是錦標賽正在準備開始新的一個回合
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (tournamentState == TournamentState.STARTING_NEW_ROUND) {
            return Language.EVENT_SUMO_EVENT_STATUS_STARTING_NEW_ROUND.toStringList(player, getUncoloredEventName(), round);
        }
        /*
         * 如果 tournamentState == TournamentState.FIGHTING, 意思就是錦標賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (tournamentState == TournamentState.FIGHTING) {
            List<String> listOfFightingPlayers = new ArrayList<>();

            for (Match match : matches) {
                String team1 = match.getTeams().get(0).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));
                String team2 = match.getTeams().get(1).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));
                listOfFightingPlayers.add(Language.EVENT_TOURNAMENT_STATUS_FIGHTING_TEAM_FORMAT.toString(team1, team2));
            }

            return Language.EVENT_SUMO_EVENT_STATUS_FIGHTING.toStringList(player, getUncoloredEventName(), round, StringUtils.join(listOfFightingPlayers, EdenPlaceholder.NEW_LINE));
        } else return null;
    }

    @Override
    public void start() {
        super.start();

        if (canEnd()) {
            end(false);
            return;
        }

        startNewRound();
    }

    @Override
    public void end(boolean forced) {
        super.end(forced);
        tournamentState = TournamentState.ENDING;

        if (forced) {
            broadcast(Language.EVENT_FORCE_CANCEL_EVENT.toString(getEventType().getName()));
            //This line of code has to be run in the last. This is to unregister the events
            destroy();
            return;
        }

        if (getParties().isEmpty()) {
            broadcast(Language.EVENT_NO_WINNER_BECAUSE_NO_PARTY.toString());
            //This line of code has to be run in the last. This is to unregister the events
            destroy();
            return;
        }

        new BukkitRunnable() {
            private final String winners = getParties().get(0).getAllPartyMembers().stream().map(PartyMember::getUsername).collect(Collectors.joining(Language.EVENT_WINNER_ANNOUNCE_SPLIT_FORMAT.toString()));
            private int count = 5;
            @Override
            public void run() {
                if (count == 0) {
                    cancel();
                    //This line of code has to be run in the last. This is to unregister the events
                    destroy();
                } else {
                    broadcast(Language.EVENT_WINNER_ANNOUNCE_MESSAGE.toString(winners));
                    count--;
                }
            }
        }.runTaskTimer(Eden.INSTANCE, 0L, 20L);
    }

    private void startNewRound() {
        round++;
        tournamentState = TournamentState.STARTING_NEW_ROUND;
        setCountdown(new EventCountdown(false,30, 30,20,15,10,5,4,3,2,1) {
            @Override
            public void runUnexpired(int tick) {
                broadcast(Language.EVENT_TOURNAMENT_NEW_ROUND_COUNTDOWN.toString(round, tick));
            }

            @Override
            public void runExpired() {
                broadcast(Language.EVENT_TOURNAMENT_NEW_ROUND_START.toStringList(round));

                List<Party> matchParties = new ArrayList<>(getParties());
                Collections.shuffle(matchParties);

                while (matchParties.size() > 1) {
                    Party party1 = matchParties.remove(0);
                    Party party2 = matchParties.remove(0);

                    ArenaDetail arena = Arena.getAvailableArenaDetail(kit);
                    if (arena == null) {
                        //This should not happen if the server has enough arena. But just in case.
                        party1.broadcast(Language.EVENT_TOURNAMENT_NEW_ROUND_NO_AVAILABLE_ARENA.toString());
                        party2.broadcast(Language.EVENT_TOURNAMENT_NEW_ROUND_NO_AVAILABLE_ARENA.toString());
                        continue;
                    }

                    Team team1 = new Team(new TeamPlayer(party1.getLeader().getPlayer()));
                    Team team2 = new Team(new TeamPlayer(party2.getLeader().getPlayer()));
                    Match match;
                    if (getTeamSize() == 1) {
                        match = new SoloMatch(arena, kit, team1, team2, QueueType.UNRANKED, true);
                    } else {
                        team1.getTeamPlayers().addAll(party1.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));
                        team2.getTeamPlayers().addAll(party2.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));
                        match = new TeamMatch(arena, kit, team1, team2);
                    }
                    match.start();
                    matches.add(match);
                }

                if (matchParties.size() == 1) {
                    matchParties.get(0).broadcast(Language.EVENT_TOURNAMENT_NEW_ROUND_AUTO_PROMOTION.toString());
                }

                tournamentState = TournamentState.FIGHTING;
            }
        });
    }

    enum TournamentState {
        NONE,
        STARTING_NEW_ROUND,
        FIGHTING,
        ENDING
    }
}
