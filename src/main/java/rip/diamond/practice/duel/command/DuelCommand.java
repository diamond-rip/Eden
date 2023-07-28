package rip.diamond.practice.duel.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.duel.DuelRequest;
import rip.diamond.practice.duel.menu.ChooseArenaMenu;
import rip.diamond.practice.duel.menu.ChooseKitMenu;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.InsertUtil;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.UUID;

public class DuelCommand extends Command {

    @CommandArgs(name = "duel")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            Language.DUEL_HELP_MESSAGE.sendListOfMessage(player);
            return;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                InsertUtil.InsertType type = InsertUtil.check(args[1]);
                UUID uuid;
                switch (type) {
                    case UUID:
                        uuid = UUID.fromString(args[1]);
                        break;
                    case STRING:
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            Language.DUEL_INVALID_PLAYER.sendMessage(player, args[1]);
                            return;
                        }
                        uuid = target.getUniqueId();
                        break;
                    default:
                        throw new PracticeUnexpectedException(type.name() + " is not a valid InsertType");
                }

                DuelRequest duelRequest = DuelRequest.getDuelRequests().get(uuid);
                if (duelRequest == null) {
                    Language.DUEL_INVALID_DUEL_REQUEST.sendMessage(player);
                    return;
                }
                plugin.getDuelRequestManager().acceptDuelRequest(duelRequest);
                DuelRequest.getDuelRequests().remove(uuid);
                return;
            }
        }

        //duel [player] [kit:optional]
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Language.DUEL_CANNOT_FIND_PLAYER.sendMessage(player, args[0]);
            return;
        }
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.DUEL_VERIFY_NEED_TO_BE_IN_LOBBY.sendMessage(player);
            return;
        }
        if (player == target) {
            Language.DUEL_CANNOT_DUEL_SELF.sendMessage(player);
            return;
        }
        Party pParty = Party.getByPlayer(player);
        Party tParty = Party.getByPlayer(target);
        if (pParty != null && tParty == null) {
            Language.DUEL_CANNOT_DUEL_NOT_IN_PARTY.sendMessage(player);
            return;
        }
        if (DuelRequest.getDuelRequests().containsKey(player.getUniqueId())) {
            Language.DUEL_HAS_PENDING_DUEL_REQUEST.sendMessage(player);
            return;
        }

        if (args.length == 1) {
            new ChooseKitMenu(target.getUniqueId(), pParty != null).openMenu(player);
            return;
        } else if (args.length == 2) {
            Kit kit = Kit.getByName(args[1]);
            if (kit == null) {
                Language.DUEL_INVALID_KIT.sendMessage(player, args[1]);
                return;
            }
            Eden.INSTANCE.getDuelRequestManager().sendDuelRequest(player, target, kit, Arena.getEnabledArena(kit));
            return;
        } else if (args.length == 3) {
            Kit kit = Kit.getByName(args[1]);
            if (kit == null) {
                Language.DUEL_INVALID_KIT.sendMessage(player, args[1]);
                return;
            }
            Arena arena = Arena.getEnabledArena(args[2], kit);
            if (arena == null) {
                Language.DUEL_INVALID_ARENA.sendMessage(player, args[2]);
                return;
            }
            Eden.INSTANCE.getDuelRequestManager().sendDuelRequest(player, target, kit, arena);
            return;
        }
    }
}
