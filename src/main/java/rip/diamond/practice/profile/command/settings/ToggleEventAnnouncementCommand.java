package rip.diamond.practice.profile.command.settings;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class ToggleEventAnnouncementCommand extends Command {
    @CommandArgs(name = "toggleeventannouncement", permission = "eden.settings.event-announcement")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        ProfileSettings settings = ProfileSettings.EVENT_ANNOUNCEMENT;

        profile.getSettings().replace(settings, settings.getNextOption(profile.getSettings().get(settings)));
        profile.getSettings().get(settings).run(player);
        settings.runSettingsChangeEvent(player, profile);

        Language.PROFILE_SETTINGS_SUCCESSFULLY_CHANGED.sendMessage(player, settings.getName(), profile.getSettings().get(settings).getName());
    }
}
