package rip.diamond.practice.match.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class ForceEndCommand extends Command {
    @CommandArgs(name = "forceend", permission = "eden.command.forceend")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Language.MATCH_FORCE_END_NOT_ONLINE.sendMessage(player);
                return;
            }
            PlayerProfile targetProfile = PlayerProfile.get(target);
            if (targetProfile == null) {
                Language.MATCH_FORCE_END_PROFILE_NOT_FOUND.sendMessage(player);
                return;
            }
            Match match = targetProfile.getMatch();
            if (match == null) {
                Language.MATCH_FORCE_END_NOT_IN_MATCH.sendMessage(player);
                return;
            }
            match.end(true, Language.MATCH_FORCE_END_REASON.toString());
            Language.MATCH_FORCE_END_SUCCESS.sendMessage(player, target.getName());
            return;
        }

        Common.sendMessage(player, CC.RED + "/forceend <player>");
    }
}
