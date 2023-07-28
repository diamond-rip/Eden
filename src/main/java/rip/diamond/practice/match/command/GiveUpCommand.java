package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class GiveUpCommand extends Command {
    @CommandArgs(name = "giveup", aliases = {"leave", "l"})
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Match match = profile.getMatch();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && match != null && match.getTeamPlayer(player) != null) {
            match.die(player, true);
            plugin.getScoreboardHandler().getScoreboard(player).unregisterHealthObjective();
            plugin.getLobbyManager().sendToSpawnAndReset(player);
        }
    }
}
