package rip.diamond.practice.party;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyInvite {

	@Getter private final UUID uuid;
	@Getter private final String username;
	private final long expiresAt = System.currentTimeMillis() + 30000L;

	public PartyInvite(Player player) {
		this.uuid = player.getUniqueId();
		this.username = player.getName();
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= expiresAt;
	}

}
