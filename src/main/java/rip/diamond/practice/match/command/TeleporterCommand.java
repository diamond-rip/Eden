package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.menu.SpectateTeleportMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class TeleporterCommand extends Command {
    @CommandArgs(name = "teleporter")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Match match = profile.getMatch();

        if (match == null) {
            return;
        }

        new SpectateTeleportMenu(match).openMenu(player);
    }
}
