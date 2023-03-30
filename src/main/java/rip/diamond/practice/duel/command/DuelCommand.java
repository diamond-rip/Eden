package rip.diamond.practice.duel.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.duel.DuelRequest;
import rip.diamond.practice.duel.menu.ChooseKitMenu;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.UUID;

public class DuelCommand extends Command {

    @CommandArgs(name = "duel")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Language.DUEL_CANNOT_FIND_PLAYER.sendMessage(player, args[0]);
                return;
            }
            if (player == target) {
                Language.DUEL_CANNOT_DUEL_SELF.sendMessage(player);
                return;
            }
            if (Party.getByPlayer(player) != null && Party.getByPlayer(target) == null) {
                Language.DUEL_CANNOT_DUEL_NOT_IN_PARTY.sendMessage(player);
                return;
            }
            if (DuelRequest.getDuelRequests().containsKey(player.getUniqueId())) {
                Language.DUEL_HAS_PENDING_DUEL_REQUEST.sendMessage(player);
                return;
            }
            new ChooseKitMenu(target.getUniqueId(), Party.getByPlayer(player) != null).openMenu(player);
            return;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (!Checker.isUUID(args[1])) {
                    Language.DUEL_INVALID_UUID.sendMessage(player);
                    return;
                }
                UUID uuid = UUID.fromString(args[1]);
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

        Language.DUEL_HELP_MESSAGE.sendListOfMessage(player);
    }
}
