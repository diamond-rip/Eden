package rip.diamond.practice.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.event.EventJoinEvent;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
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
    protected final List<Party> parties = new ArrayList<>();
    protected EventState state = EventState.WAITING;

    private EventCountdown countdown;
    private Listener bukkitListener;

    public EdenEvent(String hoster, EventType eventType, int minPlayers, int maxPlayers, int teamSize) {
        this.hoster = hoster;
        this.eventType = eventType;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamSize = teamSize;

        bukkitListener = constructListener();
        if (bukkitListener != null) plugin.getServer().getPluginManager().registerEvents(bukkitListener, plugin);
    }

    public String getEventName() {
        return eventType.getName();
    }

    public String getUncoloredEventName() {
        return ChatColor.stripColor(getEventName());
    }

    public String getTeamName(Team team) {
        return team.getLeader().getUsername() + (team.getTeamPlayers().size() <= 1 ? "" : Language.EVENT_PARTY_NAME_FORMAT.toString());
    }

    public void broadcast(Clickable clickable) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerProfile profile = PlayerProfile.get(player);
            if (profile == null) {
                return;
            }
            if (profile.getSettings().get(ProfileSettings.EVENT_ANNOUNCEMENT).isEnabled()) {
                clickable.sendToPlayer(player);
            }
        });
    }

    public void broadcast(String string) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerProfile profile = PlayerProfile.get(player);
            if (profile == null) {
                return;
            }
            if (profile.getSettings().get(ProfileSettings.EVENT_ANNOUNCEMENT).isEnabled()) {
                Common.sendMessage(player, string);
            }
        });
    }

    public void broadcast(List<String> string) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerProfile profile = PlayerProfile.get(player);
            if (profile == null) {
                return;
            }
            if (profile.getSettings().get(ProfileSettings.EVENT_ANNOUNCEMENT).isEnabled()) {
                Common.sendMessage(player, string);
            }
        });
    }

    public void broadcastToEventPlayers(String string) {
        parties.forEach(party -> {
            party.getAllPartyMembers().forEach(partyMember -> partyMember.sendMessage(string));
        });
    }

    public void broadcastToEventPlayers(List<String> string) {
        parties.forEach(party -> {
            party.getAllPartyMembers().forEach(partyMember -> partyMember.sendMessage(string));
        });
    }

    public void create() {
        onGoingEvent = this;

        Clickable clickable = new Clickable(Language.EVENT_EVENT_CREATE_MESSAGE.toString(hoster, getEventName()));
        clickable.add(Language.EVENT_EVENT_CREATE_CLICKABLE_MESSAGE.toString(), Language.EVENT_EVENT_CREATE_CLICKABLE_HOVER.toString(), "/joinevent");
        broadcast(clickable);
        countdown(60);

        PlayerProfile.getProfiles().values().stream().filter(profile -> profile.getPlayer() != null && !profile.isSaving() && profile.getPlayerState() == PlayerState.IN_LOBBY).forEach(PlayerProfile::setupItems);
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
        return party.getLeader().getUsername() + (party.getPartyMembers().isEmpty() ? "" : Language.EVENT_PARTY_NAME_FORMAT.toString());
    }

    public void join(Party party) {
        parties.add(party);

        Clickable clickable = new Clickable(Language.EVENT_EVENT_JOIN_MESSAGE.toString(getPartyName(party), getEventName(), getTotalPlayers().size(), maxPlayers));
        clickable.add(Language.EVENT_EVENT_JOIN_CLICKABLE_MESSAGE.toString(), Language.EVENT_EVENT_JOIN_CLICKABLE_HOVER.toString(), "/joinevent");
        broadcast(clickable);

        EventJoinEvent event = new EventJoinEvent(party, this);
        event.call();

        if (getTotalPlayers().size() >= maxPlayers && state == EventState.WAITING) {
            broadcast(Language.EVENT_STARTING_FULL.toString());
            countdown(10);
        }
    }

    public void leave(Party party) {
        parties.remove(party);
        broadcast(Language.EVENT_EVENT_LEAVE_MESSAGE.toString(getPartyName(party), getEventName(), getTotalPlayers().size(), maxPlayers));
    }

    public void countdown(int seconds) {
        setCountdown(new EventCountdown(true, seconds, 45,30,15,10,5,4,3,2,1) {
            @Override
            public void runUnexpired(int tick) {
                Clickable clickable = new Clickable(Language.EVENT_EVENT_START_COUNTDOWN_MESSAGE.toString(getEventName(), tick));
                clickable.add(Language.EVENT_EVENT_START_COUNTDOWN_CLICKABLE_MESSAGE.toString(), Language.EVENT_EVENT_START_COUNTDOWN_CLICKABLE_HOVER.toString(), "/joinevent");
                broadcast(clickable);
            }

            @Override
            public void run() {
                if (getMinPlayers() > parties.size()) {
                    broadcast(Language.EVENT_CANCEL_NOT_ENOUGH_PLAYERS.toString());
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

        PlayerProfile.getProfiles().values().stream()
                .filter(profile -> profile.getPlayer() != null && !profile.isSaving() && profile.getPlayerState() == PlayerState.IN_LOBBY)
                .forEach(PlayerProfile::setupItems);
    }

    public void start() {
        state = EventState.RUNNING;
    }

    public void end(boolean forced) {
        state = EventState.ENDING;
        if (countdown != null) {
            countdown.cancelCountdown();
            countdown = null;
        }
    }

    public void eliminate(Party party) {
        parties.remove(party);
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
     * @return A list of string which displays in the scoreboard
     */
    public abstract List<String> getLobbyScoreboard(Player player);

    /**
     * The Scoreboard which displays to players which is in the event when the event is running or ending
     * @param player The player who will receive the scoreboard layout
     * @return A list of string which displays in the scoreboard
     */
    public abstract List<String> getInGameScoreboard(Player player);

    /**
     * Display the current event status
     * @param player The player who views the event status
     * @return A list of string which displays the current event status
     */
    public abstract List<String> getStatus(Player player);
}
