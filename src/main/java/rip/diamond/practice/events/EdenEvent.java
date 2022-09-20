package rip.diamond.practice.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import rip.diamond.practice.Eden;
import rip.diamond.practice.event.EventJoinEvent;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.util.Clickable;
import rip.diamond.practice.util.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class EdenEvent {

    private final Eden plugin = Eden.INSTANCE;
    @Getter private static EdenEvent onGoingEvent = null;

    private final String hoster;
    private final EventType eventType;
    private final int minPlayers;
    private final int maxPlayers;
    private final int teamSize;
    private final List<Party> parties = new ArrayList<>();
    private EventState state = EventState.WAITING;

    private EventCountdown countdown;
    private Listener bukkitListener;

    public EdenEvent(String hoster, EventType eventType, int minPlayers, int maxPlayers, int teamSize) {
        this.hoster = hoster;
        this.eventType = eventType;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamSize = teamSize;

        bukkitListener = constructListener();
        plugin.getServer().getPluginManager().registerEvents(bukkitListener, plugin);
    }

    public String getEventName() {
        return eventType.getName();
    }

    public void create() {
        onGoingEvent = this;

        Clickable clickable = new Clickable("&7[&b活動&7] &b" + hoster + " &f正在舉辦一個 &b" + getEventName() + " &f活動! ");
        clickable.add("&a(點我加入活動)", "&e點擊加入活動!", "/joinevent");
        Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);
        countdown(60);
    }

    public static boolean isInEvent(Player player) {
        Party party = Party.getByPlayer(player);
        return isInEvent(party);
    }

    public static boolean isInEvent(Party party) {
        return party != null && onGoingEvent != null && onGoingEvent.getParties().contains(party);
    }

    public List<Player> getTotalPlayers() {
        List<Player> players = new ArrayList<>();
        parties.forEach(party -> players.addAll(party.getAllPartyMembers().stream().map(PartyMember::getPlayer).collect(Collectors.toList())));
        return players;
    }

    private String getPartyName(Party party) {
        return party.getLeader().getUsername() + (party.getPartyMembers().isEmpty() ? "" : "的隊伍");
    }

    public void join(Party party) {
        parties.add(party);

        Clickable clickable = new Clickable("&7[&b活動&7] &b" + getPartyName(party) + " &f加入了&b" + getEventName() + " &7(&b" + getTotalPlayers().size() + "&7/&b" + maxPlayers + "&7) ");
        clickable.add("&a(點我加入活動)", "&e點擊加入活動!", "/joinevent");
        Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);

        EventJoinEvent event = new EventJoinEvent(party, this);
        event.call();

        if (getTotalPlayers().size() >= maxPlayers && state == EventState.WAITING) {
            Common.broadcastMessage("&7[&b活動&7] &f活動人數已滿, 準備開始活動...");
            countdown(10);
        }
    }

    public void leave(Party party) {
        parties.remove(party);
        Common.broadcastMessage("&7[&b活動&7] &b" + getPartyName(party) + " &c離開了&b" + getEventName() + " &7(&b" + getTotalPlayers().size() + "&7/&b" + maxPlayers + "&7)");
    }

    public void countdown(int seconds) {
        setCountdown(new EventCountdown(seconds, 45,30,15,10,5,4,3,2,1) {
            @Override
            public void runTick(int tick) {
                Clickable clickable = new Clickable("&7[&b活動&7] &b" + getEventName() + " &f將會在 &b&l" + tick + " &f秒後開始 ");
                clickable.add("&a(點我加入活動)", "&e點擊加入活動!", "/joinevent");
                Bukkit.getOnlinePlayers().forEach(clickable::sendToPlayer);
            }

            @Override
            public void run() {
                if (getMinPlayers() > getTotalPlayers().size()) {
                    Common.broadcastMessage("&7[&b活動&7] &c參加人數不足! 活動已強制終止");
                    destroy();
                    return;
                }
                start();
            }
        });
    }

    public void destroy() {
        if (countdown != null) {
            countdown.cancelCountdown();
            countdown = null;
        }
        if (bukkitListener != null) {
            HandlerList.unregisterAll(bukkitListener);
        }
        onGoingEvent = null;
    }

    public void start() {
        state = EventState.RUNNING;
    }

    public void end() {
        state = EventState.ENDING;
        if (countdown != null) {
            countdown.cancelCountdown();
            countdown = null;
        }
    }

    public void setCountdown(EventCountdown countdown) {
        if (this.countdown != null) {
            this.countdown.cancelCountdown();
        }
        this.countdown = countdown;
    }

    public abstract Listener constructListener();

    /**
     * The Scoreboard which displays to everyone who's in the lobby (Their profile state should be IN_LOBBY)
     * @param player The player who will receive the scoreboard layout
     * @return The list of string which displays in the scoreboard
     */
    public abstract List<String> getLobbyScoreboard(Player player);

    /**
     * The Scoreboard which displays to players which is in the event when the event is running or ending
     * @param player The player who will receive the scoreboard layout
     * @return The list of string which displays in the scoreboard
     */
    public abstract List<String> getInGameScoreboard(Player player);
}
