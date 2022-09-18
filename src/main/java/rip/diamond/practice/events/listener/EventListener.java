package rip.diamond.practice.events.listener;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.event.PartyDisbandEvent;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.Common;

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
                "/settings",
                "/editkits",
                "/party info",
                "/party disband",
                "/party leave",
                "/event"
        );
        for (String cmd : bypassCommand) {
            if (event.getMessage().toLowerCase().startsWith(cmd)) {
                return;
            }
        }

        if (edenEvent.getParties().contains(party)) {
            Common.sendMessage(player, "&c你不能在活動的時候使用這個指令");
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
            party.broadcast("&e由於隊伍已解散, 你的隊伍已自動退出活動");
        }
    }

}
