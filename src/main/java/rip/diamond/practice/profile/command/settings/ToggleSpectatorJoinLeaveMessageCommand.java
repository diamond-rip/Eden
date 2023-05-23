package rip.diamond.practice.profile.command.settings;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class ToggleSpectatorJoinLeaveMessageCommand extends Command {
    @CommandArgs(name = "togglespectatorjoinleavemessage", permission = "eden.settings.spectator-join-leave-message")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        ProfileSettings settings = ProfileSettings.SPECTATOR_JOIN_LEAVE_MESSAGE;

        profile.getSettings().replace(settings, settings.getNextOption(profile.getSettings().get(settings)));
        profile.getSettings().get(settings).run(player);
        settings.runSettingsChangeEvent(player, profile);

        Language.PROFILE_SETTINGS_SUCCESSFULLY_CHANGED.sendMessage(player, settings.getName(), profile.getSettings().get(settings).getName());
    }
}
