package rip.diamond.practice.party;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Common;

import java.util.List;
import java.util.UUID;

@Getter
public class PartyMember {

    private final UUID uniqueID;
    private final String username;
    private boolean partyChat = false;

    public PartyMember(Player player) {
        this.uniqueID = player.getUniqueId();
        this.username = player.getName();
    }

    public PlayerProfile getProfile() {
        return PlayerProfile.get(uniqueID);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueID);
    }

    public void sendMessage(String... messages) {
        if (getPlayer() == null) {
            return;
        }
        Common.sendMessage(getPlayer(), messages);
    }

    public void sendMessage(List<String> messages) {
        if (getPlayer() == null) {
            return;
        }
        Common.sendMessage(getPlayer(), messages);
    }

    public void toggleChat() {
        this.partyChat = !this.partyChat;

        Language.PARTY_TOGGLE_PARTY_CHAT.sendMessage(getPlayer(), this.partyChat ? Language.ENABLED.toString() : Language.DISABLED.toString());
    }
}
