package rip.diamond.practice.leaderboard.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class ReloadLeaderboardCommand extends Command {
    @CommandArgs(name = "reloadleaderboard", permission = "practice.command.reloadleaderboard", aliases = {"reloadlb"}, async = true)
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        Language.LEADERBOARD_RELOAD.sendMessage(player);
        Eden.INSTANCE.getLeaderboardManager().update();
    }
}
