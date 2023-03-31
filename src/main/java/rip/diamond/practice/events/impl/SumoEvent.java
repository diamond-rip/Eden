package rip.diamond.practice.events.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.EdenPlaceholder;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.event.MatchPlayerDeathEvent;
import rip.diamond.practice.event.MatchRoundStartEvent;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventCountdown;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.impl.SumoEventMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
public class SumoEvent extends EdenEvent {

    private SumoEventState sumoEventState = SumoEventState.NONE;
    private SumoEventMatch match;
    private int round = 0;
    private Team teamA, teamB;

    public SumoEvent(String hoster, int minPlayers, int maxPlayers, int teamSize) {
        super(hoster, EventType.SUMO_EVENT, minPlayers, maxPlayers, teamSize);
    }

    private boolean canEnd() {
        return getState() == EventState.RUNNING && getParties().size() <= 1;
    }

    public boolean isFighting(Team team) {
        return teamA == team || teamB == team;
    }

    @Override
    public Listener constructListener() {
        return new Listener() {
            //Use this event as detecting when the match start countdown is ended
            @EventHandler
            public void onStart(MatchRoundStartEvent event) {
                if (event.getMatch() == match) {
                    startNewRound();
                }
            }

            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);

                    if (isFighting(team) && sumoEventState == SumoEventState.STARTING_NEW_ROUND) {
                        Util.teleport(player, event.getFrom());
                    }
                }
            }

            @EventHandler
            public void onDamage(EntityDamageEvent event) {
                if (!(event.getEntity() instanceof Player)) {
                    return;
                }
                Player player = (Player) event.getEntity();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);
                    TeamPlayer teamPlayer = match.getTeamPlayer(player);

                    if (!isFighting(team)) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setDamage(0);
                }
            }

            @EventHandler
            public void onDeath(MatchPlayerDeathEvent event) {
                Player player = event.getPlayer();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null && profile.getMatch() instanceof SumoEventMatch) {
                    Match match = profile.getMatch();
                    Team team = match.getTeam(player);
                    TeamPlayer teamPlayer = match.getTeamPlayer(player);

                    if (team.isEliminated()) {
                        broadcast(Language.EVENT_SUMO_EVENT_MATCH_END_MESSAGE.toString(match.getOpponentTeam(team).getLeader().getUsername(), team.getLeader().getUsername()));

                        Party party = Party.getByPlayer(team.getLeader().getPlayer());
                        //如果玩家在戰鬥時退出伺服器的話, Party 可能會是null
                        if (party != null) {
                            eliminate(party);
                        }

                        if (canEnd()) {
                            end(false);
                            return;
                        }
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
         * 如果 sumoEventState == SumoEventState.NONE, 意思就是相撲比賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (sumoEventState == SumoEventState.NONE) {
            return Language.EVENT_SUMO_EVENT_LOBBY_SCOREBOARD_STARTING_EVENT.toStringList(player);
        }
        /*
         * 如果 sumoEventState == SumoEventState.STARTING_NEW_ROUND 或者 SumoEventState.FIGHTING, 意思就是相撲比賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        if (sumoEventState == SumoEventState.STARTING_NEW_ROUND || sumoEventState == SumoEventState.FIGHTING) {
            return Language.EVENT_SUMO_EVENT_LOBBY_SCOREBOARD_FIGHTING.toStringList(player, round, teamA.getLeader().getUsername(), teamB.getLeader().getUsername());
        }
        return ImmutableList.of(EdenPlaceholder.SKIP_LINE);
    }

    @Override
    public List<String> getInGameScoreboard(Player player) {
        if (state == EventState.RUNNING) {
            /*
             * 如果 sumoEventState == SumoEventState.NONE, 意思就是相撲比賽還沒開始
             * 這個情況下, getState() 應該會回傳 EventState.WAITING
             */
            if (sumoEventState == SumoEventState.NONE) {
                return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_STARTING_MATCH.toStringList(player);
            }
            /*
             * 如果 sumoEventState == SumoEventState.STARTING_NEW_ROUND 或者 SumoEventState.FIGHTING, 意思就是相撲比賽回合已經開始, 活動內的玩家正在戰鬥中
             * 這個情況下, getState() 應該會回傳 EventState.RUNNING
             *
             * 這裏比較特別, 因為每輪戰鬥結束的時候, sumoEventState 都會是 SumoEventState.ENDING, 不代表整個活動已經結束, 所以當 sumoEventState == SumoEventState.ENDING 我們也可以顯示正在戰鬥的計分版
             */
            if (sumoEventState == SumoEventState.STARTING_NEW_ROUND || sumoEventState == SumoEventState.FIGHTING || sumoEventState == SumoEventState.ENDING) {
                return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_FIGHTING.toStringList(player, getTeamName(teamA), getTeamName(teamB));
            }
        }

        //這裏就是代表整個活動已經結束的時候要顯示的東西
        if (state == EventState.ENDING) {
            return Language.EVENT_SUMO_EVENT_IN_GAME_SCOREBOARD_ENDING.toStringList(player);
        }
        return null;
    }

    @Override
    public List<String> getStatus(Player player) {
        /*
         * 如果 tournamentState == TournamentState.NONE, 意思就是錦標賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (sumoEventState == SumoEventState.NONE) {
            return Language.EVENT_TOURNAMENT_STATUS_STARTING_EVENT.toStringList(player, getUncoloredEventName());
        }
        /*
         * 如果 tournamentState == TournamentState.STARTING_NEW_ROUND, 意思就是錦標賽正在準備開始新的一個回合
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (sumoEventState == SumoEventState.STARTING_NEW_ROUND) {
            return Language.EVENT_TOURNAMENT_STATUS_STARTING_NEW_ROUND.toStringList(player, getUncoloredEventName(), round);
        }
        /*
         * 如果 tournamentState == TournamentState.FIGHTING, 意思就是錦標賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (sumoEventState == SumoEventState.FIGHTING) {
            return Language.EVENT_TOURNAMENT_STATUS_FIGHTING.toStringList(player, getUncoloredEventName(), round, getTeamName(teamA), getTeamName(teamB));
        } else return null;
    }

    @Override
    public void start() {
        super.start();

        String kitName = Config.EVENT_SUMO_EVENT_KIT.toString();
        Kit kit = Kit.getByName(kitName);
        if (kit == null) {
            broadcastToEventPlayers("&c[Eden] Unable to find a kit named " + kitName + ", please contact an administrator.");
            end(true);
            return;
        }
        List<String> arenaNames = Config.EVENT_SUMO_EVENT_ARENAS.toStringList();
        Arena arena = Arena.getArena(arenaNames.get(new Random().nextInt(arenaNames.size())));
        ArenaDetail arenaDetail = Arena.getArenaDetail(arena);
        if (arenaDetail == null) {
            broadcastToEventPlayers("&c[Eden] Unable to find a usable arena, please contact an administrator.");
            end(true);
            return;
        }
        List<Team> teams = new ArrayList<>();
        parties.forEach(party -> party.getAllPartyMembers().forEach(partyMember -> teams.add(new Team(new TeamPlayer(partyMember.getPlayer())))));
        match = new SumoEventMatch(this, arenaDetail, kit, teams);
        match.start();

        if (canEnd()) {
            end(false);
            return;
        }
    }

    @Override
    public void end(boolean forced) {
        super.end(forced);
        sumoEventState = SumoEventState.ENDING;

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

        List<Team> teams = match.getTeams().stream().filter(team -> !team.isEliminated()).collect(Collectors.toList());
        Collections.shuffle(teams);
        teamA = teams.get(0);
        teamB = teams.get(1);

        teamA.teleport(match.getArenaDetail().getA());
        teamB.teleport(match.getArenaDetail().getB());

        //We need to set the teamA and teamB first before setting the SumoEventState. This is to prevent scoreboard throw an NPE error.
        sumoEventState = SumoEventState.STARTING_NEW_ROUND;

        setCountdown(new EventCountdown(false,5, 5,4,3,2,1) {
            @Override
            public void runUnexpired(int tick) {
                broadcastToEventPlayers(Language.EVENT_SUMO_EVENT_NEW_ROUND_COUNTDOWN.toString(round, tick));
            }
            @Override
            public void runExpired() {
                sumoEventState = SumoEventState.FIGHTING;
                broadcastToEventPlayers(Language.EVENT_SUMO_EVENT_NEW_ROUND_STARTED.toString());
            }
        });
    }

    @Override
    public void eliminate(Party party) {
        super.eliminate(party);
        sumoEventState = SumoEventState.ENDING;

        party.teleport(match.getArenaDetail().getSpectator());

        setCountdown(new EventCountdown(true, 3) {
            @Override
            public void runUnexpired(int tick) {

            }

            @Override
            public void runExpired() {
                if (canEnd()) {
                    end(false);
                    return;
                }
                startNewRound();
            }
        });
    }

    enum SumoEventState {
        NONE,
        STARTING_NEW_ROUND,
        FIGHTING,
        ENDING
    }
}
