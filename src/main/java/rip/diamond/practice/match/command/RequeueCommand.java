package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class RequeueCommand extends Command {
    @CommandArgs(name = "requeue")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        Match match = profile.getMatch();
        if (match == null) {
            Language.MATCH_REQUEUE_NOT_IN_MATCH.sendMessage(player);
            return;
        }
        if (match.getState() != MatchState.ENDING) {
            return;
        }
        plugin.getLobbyManager().sendToSpawnAndReset(player);
        Tasks.runLater(() -> Queue.joinQueue(player, match.getKit(), match.getQueueType()), 1L);
    }
}
