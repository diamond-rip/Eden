package rip.diamond.practice.events.impl;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.event.MatchEndEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventCountdown;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tournament extends EdenEvent {

    private final Kit kit;
    private final List<Match> matches = new ArrayList<>();
    private int round = 0;

    public Tournament(String hoster, int minPlayers, int maxPlayers, Kit kit, int teamSize) {
        super(hoster, EventType.TOURNAMENT, minPlayers, maxPlayers, teamSize);

        this.kit = kit;
    }

    private String getTeamName(Team team) {
        return team.getLeader().getUsername() + (team.getTeamPlayers().size() <= 1 ? "" : "的隊伍");
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

                    if (getParties().size() <= 1) {
                        end();
                        return;
                    }

                    if (matches.isEmpty()) {
                        startNewRound();
                    }
                }
            }
        };
    }

    @Override
    public void start() {
        super.start();
        startNewRound();
    }

    @Override
    public void end() {
        super.end();

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
            }
        });
    }
}
