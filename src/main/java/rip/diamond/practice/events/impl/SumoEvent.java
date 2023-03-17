package rip.diamond.practice.events.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventCountdown;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.events.EventType;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SumoEvent extends EdenEvent {

    private SumoEventState sumoEventState = SumoEventState.NONE;
    private int round = 0;
    private Party partyA, partyB;
    private Location a, b, spectator;

    public SumoEvent(String hoster, int minPlayers, int maxPlayers, int teamSize) {
        super(hoster, EventType.SUMO_EVENT, minPlayers, maxPlayers, teamSize);

        try {
            a = LocationSerialization.deserializeLocation(Eden.INSTANCE.getLocationFile().getString("sumo-event.a"));
            b = LocationSerialization.deserializeLocation(Eden.INSTANCE.getLocationFile().getString("sumo-event.b"));
            spectator = LocationSerialization.deserializeLocation(Eden.INSTANCE.getLocationFile().getString("sumo-event.spectator"));
        } catch (Exception e) {
            e.printStackTrace();
            broadcastToEventPlayers("&c[Eden] An error occurred while setting up SumoEvent's location. Please contact an administrator for more information.");
            end(true);
        }
    }

    private boolean canEnd() {
        return getState() == EventState.RUNNING && getParties().size() <= 1;
    }

    @Override
    public Listener constructListener() {
        return new Listener() {
            @EventHandler
            public void onMove(PlayerMoveEvent event) {
                if (partyA == null || partyB == null) {
                    return;
                }

                Player player = event.getPlayer();
                Block block = event.getTo().getBlock();

                boolean isInPartyA = partyA.getMember(player) != null;
                boolean isInPartyB = partyB.getMember(player) != null;
                if (isInPartyA || isInPartyB) {
                    if (sumoEventState == SumoEventState.STARTING_NEW_ROUND) {
                        Util.teleport(player, isInPartyA ? a : b);
                    } else if (sumoEventState == SumoEventState.FIGHTING && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)) {
                        eliminate(isInPartyA ? partyA : partyB);
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
    public String getNameTagPrefix(Player target, Player viewer) {
        return CC.WHITE;
    }

    @Override
    public List<String> getLobbyScoreboard(Player player) {
        /*
         * 如果 sumoEventState == SumoEventState.NONE, 意思就是錦標賽還沒開始
         * 這個情況下, getState() 應該會回傳 EventState.WAITING
         */
        if (sumoEventState == SumoEventState.NONE) {
            return Language.EVENT_SUMO_EVENT_SCOREBOARD_STARTING_EVENT.toStringList(player);
        }
        /*
         * 如果 sumoEventState == SumoEventState.STARTING_NEW_ROUND 或者 SumoEventState.FIGHTING, 意思就是錦標賽回合已經開始, 活動內的玩家正在戰鬥中
         * 這個情況下, getState() 應該會回傳 EventState.RUNNING
         */
        else if (sumoEventState == SumoEventState.STARTING_NEW_ROUND || sumoEventState == SumoEventState.FIGHTING) {
            return Language.EVENT_SUMO_EVENT_SCOREBOARD_FIGHTING.toStringList(player, round, partyA.getLeader().getUsername(), partyB.getLeader().getUsername());
        } else return new ArrayList<>();
    }

    @Override
    public List<String> getInGameScoreboard(Player player) {
        return null;
    }

    @Override
    public List<String> getStatus(Player player) {
        return null;
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
        sumoEventState = SumoEventState.STARTING_NEW_ROUND;

        Collections.shuffle(parties);
        partyA = parties.get(0);
        partyB = parties.get(1);

        partyA.teleport(a);
        partyB.teleport(b);

        setCountdown(new EventCountdown(5, 5,4,3,2,1) {
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

    private void eliminate(Party party) {
        sumoEventState = SumoEventState.ENDING;

        party.teleport(spectator);
        parties.remove(party);

        Party winner = party == partyA ? partyB : partyA;
        broadcastToEventPlayers(Language.EVENT_SUMO_EVENT_MATCH_END_MESSAGE.toString(winner.getLeader().getUsername(), party.getLeader().getUsername()));

        setCountdown(new EventCountdown(3) {
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
