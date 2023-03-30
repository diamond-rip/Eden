package rip.diamond.practice.duel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.impl.SoloMatch;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.queue.QueueType;

import java.util.stream.Collectors;

public class DuelRequestManager {

    public void sendDuelRequest(Player sender, Player target, Kit kit, Arena arena) {
        if (!verify(sender, target, kit, arena)) {
            return;
        }
        Party party = Party.getByPlayer(sender);
        new DuelRequest(sender.getUniqueId(), target.getUniqueId(), party != null, kit, arena).send();
    }

    public void acceptDuelRequest(DuelRequest duelRequest) {
        Player sender = Bukkit.getPlayer(duelRequest.getSenderUUID());
        Player target = Bukkit.getPlayer(duelRequest.getTargetUUID());
        Kit kit = duelRequest.getKit();
        Arena arena = duelRequest.getArena();

        if (!verify(sender, target, kit, arena)) {
            return;
        }

        ArenaDetail arenaDetail = Arena.getArenaDetail(arena);

        Team team1 = new Team(new TeamPlayer(sender));
        Team team2 = new Team(new TeamPlayer(target));

        if (duelRequest.isParty()) {
            Party party1 = Party.getByPlayer(sender);
            Party party2 = Party.getByPlayer(target);

            team1.getTeamPlayers().addAll(party1.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));
            team2.getTeamPlayers().addAll(party2.getPartyMembers().stream().map(partyMember -> new TeamPlayer(partyMember.getPlayer())).collect(Collectors.toList()));

            TeamMatch match = new TeamMatch(arenaDetail, kit, team1, team2);
            match.start();
        } else {
            SoloMatch match = new SoloMatch(arenaDetail, kit, team1, team2, QueueType.UNRANKED, true);
            match.start();
        }
    }

    public boolean verify(Player sender, Player target, Kit kit, Arena arena) {
        if (target == null) {
            Language.DUEL_VERIFY_TARGET_NOT_FOUND.sendMessage(sender);
            return false;
        }
        if (sender == target) {
            Language.DUEL_CANNOT_DUEL_SELF.sendMessage(sender);
            return false;
        }
        PlayerProfile profile = PlayerProfile.get(sender);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.DUEL_VERIFY_NEED_TO_BE_IN_LOBBY.sendMessage(sender);
            return false;
        }

        PlayerProfile targetProfile = PlayerProfile.get(target);
        if (targetProfile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.DUEL_VERIFY_TARGET_NEED_TO_BE_IN_LOBBY.sendMessage(sender);
            return false;
        }
        if (!targetProfile.getSettings().get(ProfileSettings.ALLOW_DUEL_REQUEST).isEnabled()) {
            Language.DUEL_VERIFY_TARGET_DUEL_REQUEST_DISABLED.sendMessage(sender);
            return false;
        }

        if (arena.isEdited()) {
            Language.DUEL_VERIFY_ARENA_DISABLED.sendMessage(sender);
            return false;
        }

        ArenaDetail arenaDetail = Arena.getArenaDetail(arena);
        if (arenaDetail == null) {
            Language.DUEL_VERIFY_CANNOT_FIND_ARENA.sendMessage(sender);
            return false;
        }

        Party party1 = Party.getByPlayer(sender);
        Party party2 = Party.getByPlayer(target);
        if (party1 == null && party2 != null) {
            Language.DUEL_VERIFY_TARGET_IN_A_PARTY.sendMessage(sender);
            return false;
        }
        if (party1 != null && party2 == null) {
            Language.DUEL_VERIFY_TARGET_NOT_IN_A_PARTY.sendMessage(sender);
            return false;
        }

        boolean isPartyDuelRequest = party1 != null;
        if (isPartyDuelRequest) {
            if (party1 == party2) {
                Language.DUEL_VERIFY_CANNOT_DUEL_SAME_PARTY.sendMessage(sender);
                return false;
            }
            if (!party1.getLeader().getUniqueID().equals(sender.getUniqueId())) {
                Language.PARTY_ONLY_LEADER.sendMessage(sender);
                return false;
            }
            if (!party1.isAllPlayersInState(PlayerState.IN_LOBBY)) {
                Language.DUEL_VERIFY_PLAYER_NOT_IN_LOBBY.sendMessage(sender, party1.getLeader().getUsername());
                return false;
            }
            if (!party2.isAllPlayersInState(PlayerState.IN_LOBBY)) {
                Language.DUEL_VERIFY_PLAYER_NOT_IN_LOBBY.sendMessage(sender, party2.getLeader().getUsername());
                return false;
            }
        }
        return true;
    }

}
