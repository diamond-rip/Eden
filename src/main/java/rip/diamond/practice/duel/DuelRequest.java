package rip.diamond.practice.duel;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.EdenSound;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.duel.task.DuelRequestClearTask;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.Clickable;
import rip.diamond.practice.util.Common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DuelRequest {

	@Getter private static final Map<UUID, DuelRequest> duelRequests = new HashMap<>();

	@Getter private final UUID senderUUID;
	@Getter private final UUID targetUUID;
	@Getter private final String targetName;
	@Getter private final boolean party;
	@Getter private final Kit kit;
	@Getter private final Arena arena;
	private final long createdAt = System.currentTimeMillis();

	public static void init() {
		new DuelRequestClearTask();
	}

	public DuelRequest(UUID senderUUID, UUID targetUUID, boolean party, Kit kit, Arena arena) {
		this.senderUUID = senderUUID;
		this.targetUUID = targetUUID;
		this.targetName = Bukkit.getPlayer(targetUUID).getName();
		this.party = party;
		this.kit = kit;
		this.arena = arena;

		duelRequests.put(senderUUID, this);
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - createdAt >= 30000L;
	}

	public DuelRequest send() {
		Player sender = Bukkit.getPlayer(senderUUID);
		Player target = Bukkit.getPlayer(targetUUID);

		if (sender == null) {
			return this;
		}
		if (target == null) {
			Language.DUEL_DUEL_REQUEST_CANNOT_FIND_TARGET.sendMessage(sender);
			return this;
		}

		String ping = isParty() ? Party.getByPlayer(sender).getAllPartyMembers().stream().map(partyMember -> formatPing(partyMember.getPlayer())).collect(Collectors.joining("\n")) : formatPing(sender);

		Clickable clickable = new Clickable(party ? Language.DUEL_DUEL_REQUEST_DISPLAY_PARTY.toString(sender.getName(), kit.getDisplayName(), arena.getDisplayName(), Party.getByPlayer(sender).getAllPartyMembers().size()) : Language.DUEL_DUEL_REQUEST_DISPLAY_1V1.toString(sender.getName(), kit.getDisplayName(), arena.getDisplayName()));
		clickable.add(Language.DUEL_DUEL_REQUEST_CLICK_TO_ACCEPT.toString(), Language.DUEL_DUEL_REQUEST_CLICK_TO_ACCEPT_HOVER.toString(), "/duel accept " + senderUUID);
		clickable.add(" ");
		clickable.add(Language.DUEL_DUEL_REQUEST_CLICK_TO_VIEW_PING.toString(), ping, null);
		clickable.sendToPlayer(target);
		EdenSound.RECEIVE_DUEL_REQUEST.play(target);

		if (party) {
			Language.DUEL_DUEL_REQUEST_SUCCESS_PARTY.sendMessage(sender, kit.getDisplayName(), arena.getDisplayName(), target.getName());
		} else {
			Language.DUEL_DUEL_REQUEST_SUCCESS_1V1.sendMessage(sender, kit.getDisplayName(), arena.getDisplayName(), target.getName());
		}
		return this;
	}

	private String formatPing(Player player) {
		return Language.DUEL_DUEL_REQUEST_CLICK_TO_VIEW_PING_HOVER.toString(player.getName(), player.spigot().getPing());
	}

}
