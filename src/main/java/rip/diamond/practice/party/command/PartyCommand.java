package rip.diamond.practice.party.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.PartyMember;
import rip.diamond.practice.party.PartyPrivacy;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class PartyCommand extends Command {
    @CommandArgs(name = "party", aliases = {"team", "p"})
    public void execute(CommandArguments command) {
       Player player = command.getPlayer();
       String[] args = command.getArgs();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                if (Party.getByPlayer(player) != null) {
                    Language.PARTY_IN_A_PARTY.sendMessage(player);
                    return;
                }

                PlayerProfile profile = PlayerProfile.get(player);
                if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
                    Language.PARTY_WRONG_STATE.sendMessage(player);
                    return;
                }

                Party party = new Party(player, Config.PARTY_DEFAULT_MAX_SIZE.toInteger());
                return;
            }
            else if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("disband")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    party.disband(false);
                } else {
                    party.leave(player.getUniqueId(), false);
                }
                return;
            }
            else if (args[0].equalsIgnoreCase("open")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                party.setPrivacy(PartyPrivacy.OPEN);
                return;
            }
            else if (args[0].equalsIgnoreCase("close")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                party.setPrivacy(PartyPrivacy.CLOSED);
                return;
            }
            else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                party.getMember(player).toggleChat();
                return;
            }
            else if (args[0].equalsIgnoreCase("mute")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                party.toggleChatRoom();
                return;
            }
            else if (args[0].equalsIgnoreCase("announce")) {
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                PlayerProfile profile = PlayerProfile.get(player);
                if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
                    Language.PARTY_WRONG_STATE.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                if (!player.hasPermission("eden.party.announce")) {
                    Language.NO_PERMISSION.sendMessage(player);
                    return;
                }
                if (party.getPrivacy() != PartyPrivacy.OPEN) {
                    Language.PARTY_PARTY_NOT_OPEN.sendMessage(player);
                    return;
                }
                if (party.getLastAnnounced() + (Config.PARTY_ANNOUNCE_COOLDOWN.toInteger() * 1000L) > System.currentTimeMillis()) {
                    Language.PARTY_ANNOUNCE_COOLDOWN.sendMessage(player, Config.PARTY_ANNOUNCE_COOLDOWN);
                    return;
                }
                party.announce();
                return;
            }
            else if (args[0].equalsIgnoreCase("list")) {
                Util.performCommand(player, "party list " + player.getName());
                return;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                PlayerProfile profile = PlayerProfile.get(player);
                if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
                    Language.PARTY_WRONG_STATE.sendMessage(player);
                    return;
                }
                if (Party.getByPlayer(player) != null) {
                    Language.PARTY_IN_A_PARTY.sendMessage(player);
                    return;
                }
                Party party = Party.getByPlayer(target);
                if (party == null) {
                    Language.PARTY_PARTY_NOT_FOUND.sendMessage(player, target.getName());
                    return;
                }
                if (party.isFull() && !player.hasPermission("eden.party.forcejoin")) {
                    Language.PARTY_FULL.sendMessage(player);
                    return;
                }
                if (party.getAllPartyMembers().stream().map(PartyMember::getUniqueID).anyMatch(uuid -> player.getUniqueId().equals(uuid))) {
                    Language.PARTY_ALREADY_IN_PARTY.sendMessage(player);
                    return;
                }
                if (party.getInvites().containsKey(player.getUniqueId())) {
                    party.getInvites().remove(player.getUniqueId());
                    party.join(player, false);
                } else if (party.getPrivacy() == PartyPrivacy.OPEN) {
                    party.join(player, false);
                } else if (player.hasPermission("eden.party.forcejoin")){
                    party.join(player, true);
                } else {
                    Language.PARTY_NOT_INVITED.sendMessage(player);
                }
                return;
            } else if (args[0].equalsIgnoreCase("list")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                Party party = Party.getByPlayer(target);
                if (party == null) {
                    Language.PARTY_PARTY_NOT_FOUND.sendMessage(player, target.getName());
                    return;
                }
                party.sendInformation(player);
                return;
            } else if (args[0].equalsIgnoreCase("invite")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                if (player == target) {
                    Language.PARTY_CANNOT_INTERACT_SELF.sendMessage(player);
                    return;
                }
                PlayerProfile targetProfile = PlayerProfile.get(player);
                if (targetProfile == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                if (!targetProfile.getSettings().get(ProfileSettings.ALLOW_PARTY_INVITE).isEnabled()) {
                    Language.PARTY_DISABLED_PARTY_INVITE.sendMessage(player);
                    return;
                }

                Party party = Party.getByPlayer(player);
                if (party == null) {
                    party = new Party(player, Config.PARTY_DEFAULT_MAX_SIZE.toInteger());
                }

                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                if (party.getInvites().values().stream().filter(partyInvite -> !partyInvite.isExpired()).anyMatch(partyInvite -> partyInvite.getUuid().equals(target.getUniqueId()))) {
                    Language.PARTY_ALREADY_INVITE.sendMessage(player);
                    return;
                }
                Party targetParty = Party.getByPlayer(target);
                if (targetParty != null) {
                    Language.PARTY_TARGET_ALREADY_IN_PARTY.sendMessage(player);
                    return;
                }
                party.invite(target);
                return;
            } else if (args[0].equalsIgnoreCase("kick")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                if (player == target) {
                    Language.PARTY_CANNOT_INTERACT_SELF.sendMessage(player);
                    return;
                }
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                PartyMember partyMember = party.getMember(target);
                if (partyMember == null) {
                    Language.PARTY_PLAYER_NOT_FOUND.sendMessage(player, args[1]);
                    return;
                }
                party.leave(partyMember.getUniqueID(), true);
                return;
            } else if (args[0].equalsIgnoreCase("size")) {
                if (!Checker.isInteger(args[1])) {
                    Language.PARTY_NOT_INTEGER.sendMessage(player, args[1]);
                    return;
                }
                int size = Integer.parseInt(args[1]);
                if (size < 1) {
                    Language.PARTY_SIZE_BELOW_1.sendMessage(player);
                    return;
                }
                Party party = Party.getByPlayer(player);
                if (party == null) {
                    Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
                    return;
                }
                if (!party.getLeader().getUniqueID().equals(player.getUniqueId())) {
                    Language.PARTY_ONLY_LEADER.sendMessage(player);
                    return;
                }
                //Should not happen, but just in case
                if (party.getLeader().getPlayer() == null) {
                    Language.PARTY_ERROR_LEADER_NOT_FOUND.sendMessage(player);
                    return;
                }
                int canSetMaxSize = party.getLeader().getPlayer().getEffectivePermissions().stream().filter(permissionAttachmentInfo -> permissionAttachmentInfo.getPermission().contains("eden.party.limits.")).mapToInt(permissionAttachmentInfo -> Integer.parseInt(permissionAttachmentInfo.getPermission().replaceAll("eden.party.limits.", ""))).max().orElse(Config.PARTY_DEFAULT_MAX_SIZE.toInteger());
                if (canSetMaxSize < size) {
                    Language.PARTY_MAX_SIZE.sendMessage(player, canSetMaxSize);
                    return;
                }
                party.setMaxSize(size);
                return;
            }
        }

        Language.PARTY_HELP_MESSAGE.sendListOfMessage(player);
    }
}
