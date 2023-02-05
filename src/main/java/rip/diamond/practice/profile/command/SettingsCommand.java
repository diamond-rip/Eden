package rip.diamond.practice.profile.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.profile.menu.ProfileSettingsMenu;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class SettingsCommand extends Command {
    @CommandArgs(name = "settings", aliases = {"practicesettings", "psettings", "pracsettings"})
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();

        new ProfileSettingsMenu().openMenu(player);
    }
}
