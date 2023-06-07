package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.menu.ViewInventoryMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.UUID;

public class ViewInventoryCommand extends Command {
    @CommandArgs(name = "viewinventory", aliases = {"viewinv"})
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        String[] args = command.getArgs();

        //Fix for #344 - Allow player to view post match inventory while in match
        /*if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.MATCH_VIEW_INVENTORY_WRONG_STATE.sendMessage(player);
            return;
        }*/

        if (args.length != 1) {
            Language.MATCH_VIEW_INVENTORY_USAGE.sendMessage(player);
            return;
        }

        if (!Checker.isUUID(args[0])) {
            Language.MATCH_VIEW_INVENTORY_INVALID_UUID.sendMessage(player);
            return;
        }

        UUID uuid = UUID.fromString(args[0]);

        if (!Match.getPostMatchInventories().containsKey(uuid)) {
            Language.MATCH_VIEW_INVENTORY_CANNOT_FIND.sendMessage(player);
            return;
        }

        new ViewInventoryMenu(Match.getPostMatchInventories().get(uuid)).openMenu(player);
    }
}
