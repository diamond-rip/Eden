package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class LeaveSpectateCommand extends Command {
    @CommandArgs(name = "leavespectate")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING && profile.getMatch() != null) {
            Match match = profile.getMatch();
            match.leaveSpectate(player);
        }
    }
}
