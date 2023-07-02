package rip.diamond.practice.party.fight;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.match.impl.FFAMatch;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PartyFightManager {

    public void startPartyEvent(Player leader, KitMatchType matchType, Kit kit, Arena arena) {
        Party party = Party.getByPlayer(leader);
        if (party == null) {
            Language.PARTY_NOT_IN_A_PARTY.sendMessage(leader);
            return;
        }
        if (!party.getLeader().getUniqueID().equals(leader.getUniqueId())) {
            Language.PARTY_START_PARTY_FIGHT_ONLY_LEADER.sendMessage(leader);
            return;
        }

        if (party.getAllPartyMembers().size() < 2) {
            Language.PARTY_START_PARTY_FIGHT_NEED_MORE_THAN_2.sendMessage(leader);
            return;
        }

        if (arena.isEdited()) {
            Language.PARTY_START_PARTY_FIGHT_ARENA_DISABLED.sendMessage(leader);
            return;
        }

        ArenaDetail arenaDetail = Arena.getArenaDetail(arena);
        if (arenaDetail == null) {
            Language.PARTY_START_PARTY_FIGHT_CANNOT_FIND_ARENA.sendMessage(leader);
            return;
        }

        List<String> notInLobbyPlayers = new ArrayList<>();
        party.getAllPartyMembers().stream().filter(partyMember -> PlayerProfile.get(partyMember.getPlayer()).getPlayerState() != PlayerState.IN_LOBBY).forEach(pm -> notInLobbyPlayers.add(pm.getUsername()));
        if (!notInLobbyPlayers.isEmpty()) {
            Language.PARTY_START_PARTY_FIGHT_PLAYERS_NOT_IN_LOBBY.sendMessage(leader, StringUtils.join(notInLobbyPlayers, ", "));
            return;
        }

        switch (matchType) {
            case FFA:
                List<Team> teams = new ArrayList<>();
                party.getAllPartyMembers().forEach(partyMember -> teams.add(new Team(new TeamPlayer(partyMember.getPlayer()))));

                FFAMatch ffaMatch = new FFAMatch(arenaDetail, kit, teams);
                ffaMatch.start();
                return;
            case SPLIT:
                List<Player> players = party.getAllPartyMembers().stream().map(PartyMember::getPlayer).collect(Collectors.toList());
                Collections.shuffle(players);

                Team team1 = new Team(new TeamPlayer(players.get(0)));
                Team team2 = new Team(new TeamPlayer(players.get(1)));

                players.stream().filter(player -> !team1.containsPlayer(player) && !team2.containsPlayer(player)).forEach(player -> {
                    if (team1.getTeamPlayers().size() < team2.getTeamPlayers().size()) {
                        team1.getTeamPlayers().add(new TeamPlayer(player));
                    } else {
                        team2.getTeamPlayers().add(new TeamPlayer(player));
                    }
                });

                TeamMatch teamMatch = new TeamMatch(arenaDetail, kit, team1, team2);
                teamMatch.start();
                return;
            default:
                throw new PracticeUnexpectedException("This should not happen");
        }
    }

}
