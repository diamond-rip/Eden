package rip.diamond.practice.events.impl;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
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
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Tasks;

import java.util.*;
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

    public String getUncoloredEventName() {
        return ChatColor.stripColor(super.getEventName());
    }

    private String getTeamName(Team team) {
        return team.getLeader().getUsername() + (team.getTeamPlayers().size() <= 1 ? "" : "的隊伍");
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

                    Common.broadcastMessage("&7[&b活動&7] &a" + getTeamName(winner) + " &f擊敗了 &c" + getTeamName(loser) + " &7(剩餘 " + matches.size() + " &7場戰鬥)");

                    Party party = Party.getByPlayer(loser.getLeader().getPlayer());
                    //如果玩家在戰鬥時退出伺服器的話, Party 可能會是null
                    if (party != null) {
                        getParties().remove(party);
                    }

                    if (canEnd()) {
                        end();
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
                    end();
                }
            }
        };
    }

    @Override
    public List<String> getLobbyScoreboard(Player player) {
        String countdown = getCountdown() == null ? "-1" : getCountdown().getSecondsLeft() + "";

        /*
         * 如果 tournamentState == TournamentState.NONE, 意思就是錦標賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (tournamentState == TournamentState.NONE) {
            return Arrays.asList(
                    "&b&l" + getUncoloredEventName(),
                    " &f現時人數: &b" + getTotalPlayers().size() + "&7/&b" + getMaxPlayers(),
                    "",
                    "&f將會在 &b&l" + countdown + " &f秒後開始",
                    ""
            );
        }
        /*
         * 如果 tournamentState == TournamentState.STARTING_NEW_ROUND, 意思就是錦標賽正在準備開始新的一個回合
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (tournamentState == TournamentState.STARTING_NEW_ROUND) {
            return Arrays.asList(
                    "&b&l" + getUncoloredEventName(),
                    "",
                    "&f第 &b&l" + round + " &f回合",
                    "&7將會在 &b" + countdown + " &7秒後開始",
                    ""
            );
        }
        /*
         * 如果 tournamentState == TournamentState.FIGHTING, 意思就是錦標賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (tournamentState == TournamentState.FIGHTING) {
            return Arrays.asList(
                    "&b&l" + getUncoloredEventName(),
                    "",
                    "&f第 &b&l" + round + " &f回合",
                    "&f使用指令 &b/event status &f查看本回合的戰鬥",
                    ""
            );
        } else return new ArrayList<>();
    }

    @Override
    public List<String> getInGameScoreboard(Player player) {
        return null;
    }

    @Override
    public List<String> getStatus(Player player) {
        final List<String> toReturn = new ArrayList<>();

        toReturn.addAll(Arrays.asList(
                "",
                "&b&l" + getUncoloredEventName(),
                "&f正在進行第 &b" + round + " 回合",
                ""
        ));

        for (Match match : matches) {
            String team1 = match.getTeams().get(0).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));
            String team2 = match.getTeams().get(1).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(", "));

            toReturn.add("&b" + team1 + " &fvs " + "&b" + team2);
        }

        toReturn.add("");

        return toReturn;
    }

    @Override
    public void start() {
        super.start();
        startNewRound();
    }

    @Override
    public void end() {
        super.end();
        tournamentState = TournamentState.ENDING;
        if (getParties().isEmpty()) {
            Common.broadcastMessage("&7[&b活動&7] &c沒有派對生還, 本次活動沒有勝利者");
            destroy();
            return;
        }

        new BukkitRunnable() {
            private final String winners = getParties().get(0).getAllPartyMembers().stream().map(PartyMember::getUsername).collect(Collectors.joining("&7, &b"));
            private int count = 5;
            @Override
            public void run() {
                if (count == 0) {
                    cancel();
                    //This line of code has to be run in the last. This is to unregister the events
                    destroy();
                } else {
                    Common.broadcastMessage("&7[&b活動&7] &a勝利者: &b" + winners);
                    count--;
                }
            }
        }.runTaskTimer(Eden.INSTANCE, 0L, 20L);
    }

    private void startNewRound() {
        round++;
        tournamentState = TournamentState.STARTING_NEW_ROUND;
        setCountdown(new EventCountdown(10, 10,5,4,3,2,1) {
            @Override
            public void runTick(int tick) {
                Common.broadcastMessage("&7[&b活動&7] &f第 &b&l" + round + " &f輪錦標賽將會在 &b&l" + tick + " &f秒後開始...");
            }

            @Override
            public void run() {
                Common.broadcastMessage("", "&7[&b活動&7] &f第 &b&l" + round + " &f回合已經開始!", "");

                List<Party> matchParties = new ArrayList<>(getParties());
                Collections.shuffle(matchParties);

                while (matchParties.size() > 1) {
                    Party party1 = matchParties.remove(0);
                    Party party2 = matchParties.remove(0);

                    ArenaDetail arena = Arena.getAvailableArenaDetail(kit);
                    if (arena == null) {
                        //This should not happen if the server has enough arena. But just in case.
                        party1.broadcast("&7[&b活動&7] &c錯誤: 場地不足, 你的派對將會放置到下一個回合才會開始");
                        party2.broadcast("&7[&b活動&7] &c錯誤: 場地不足, 你的派對將會放置到下一個回合才會開始");
                        continue;
                    }

                    Team team1 = new Team(new TeamPlayer(party1.getLeader().getPlayer()));
                    Team team2 = new Team(new TeamPlayer(party2.getLeader().getPlayer()));
                    Match match;
                    if (getTeamSize() == 1) {
                        match = new SoloMatch(arena, kit, team1, team2, QueueType.UNRANKED, true);
                        Tasks.run(match::start);
                    } else {
                        team1.getTeamPlayers().addAll(party1.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));
                        team2.getTeamPlayers().addAll(party2.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));

                        match = new TeamMatch(arena, kit, team1, team2);
                        Tasks.run(match::start);
                    }
                    matches.add(match);
                }

                if (matchParties.size() == 1) {
                    matchParties.get(0).broadcast("&7[&b活動&7] &e本回合派對總數為單數, 你的隊伍已被自動晉級 &a:)");
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
