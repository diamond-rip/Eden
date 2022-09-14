package rip.diamond.practice.party;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.Common;

import java.util.UUID;

@Getter
public class PartyMember {

    private final UUID uniqueID;
    private final String username;

    public PartyMember(Player player) {
        this.uniqueID = player.getUniqueId();
        this.username = player.getName();
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

}
