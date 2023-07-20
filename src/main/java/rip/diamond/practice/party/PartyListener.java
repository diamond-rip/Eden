package rip.diamond.practice.party;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;

public class PartyListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) //ignoreCancelled because if someone is muted, this event will not perform
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        Party party = Party.getByPlayer(player);
        if (party == null) {
            return;
        }

        boolean isPartyChat = party.getMember(player).isPartyChat();
        if (message.startsWith("!") || message.startsWith("@") || isPartyChat) {
            event.setCancelled(true);
            if (party.isMuted() && !party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                Language.PARTY_CHAT_OFF.sendMessage(player);
                return;
            }
            party.broadcast(Language.PARTY_PARTY_CHAT_FORMAT.toString(player.getName(), ChatColor.stripColor(isPartyChat ? event.getMessage() : event.getMessage().substring(1))));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        //Profile will be null if the profile is not loaded in PlayerJoinEvent
        if (profile == null) {
            return;
        }
        Party party = Party.getByPlayer(player);
        if (party == null) {
            return;
        }

        if (party.getLeader().getUniqueID().equals(player.getUniqueId())) {
            party.broadcast(Language.PARTY_DISBAND_LEADER_LEFT.toString());
            party.disband(false);
        } else {
            party.broadcast(Language.PARTY_QUIT.toString(player.getName()));
            party.leave(player.getUniqueId(), false);
        }
    }

}
