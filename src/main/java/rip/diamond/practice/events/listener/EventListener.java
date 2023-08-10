package rip.diamond.practice.events.listener;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.event.MatchStartEvent;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.event.PartyJoinEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.party.Party;

import java.util.List;

@RequiredArgsConstructor
public class EventListener implements Listener {

    private final Eden plugin;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        EdenEvent edenEvent = EdenEvent.getOnGoingEvent();
        if (edenEvent == null) {
            return;
        }

        Party party = Party.getByPlayer(player);
        if (party == null) {
            return;
        }

        List<String> bypassCommand = ImmutableList.of(
                "/test",
                "/settings",
                "/editkits",
                "/party list",
                "/party disband",
                "/party leave",
                "/event",
                "/viewinventory"
        );
        for (String cmd : bypassCommand) {
            String message = event.getMessage().toLowerCase().replace("eden:", "");
            if (message.startsWith(cmd)) {
                return;
            }
        }

        if (edenEvent.getParties().contains(party)) {
            Language.EVENT_CANNOT_USE_THIS_COMMAND.sendMessage(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDisband(PartyDisbandEvent event) {
        Party party = event.getParty();
        EdenEvent edenEvent = EdenEvent.getOnGoingEvent();
        if (edenEvent == null) {
            return;
        }
        if (edenEvent.getParties().contains(party)) {
            edenEvent.getParties().remove(party);
            party.broadcast(Language.EVENT_LEAVE_EVENT_BECAUSE_PARTY_DISBAND.toString());
        }
    }

    @EventHandler
    public void onJoinParty(PartyJoinEvent event) {
        Party party = event.getParty();
        EdenEvent edenEvent = EdenEvent.getOnGoingEvent();
        if (edenEvent == null) {
            return;
        }

        if (edenEvent.getParties().contains(party)) {
            event.setCancelled(true, Language.EVENT_CANNOT_JOIN_PARTY_BECAUSE_IN_EVENT.toString());
        }
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Match match = event.getMatch();
        EdenEvent edenEvent = EdenEvent.getOnGoingEvent();
        if (edenEvent != null && (edenEvent.getState() == EventState.WAITING)) {
            match.getMatchPlayers().forEach(player -> {
                Party party = Party.getByPlayer(player);
                boolean exists = edenEvent.getParties().removeIf(p -> p == party);
                if (exists) {
                    party.broadcast("&c錯誤: 你已被本活動移除", "&c這是一個系統錯誤, 請回報給系統管理員");
                }
            });
        }
    }

}
