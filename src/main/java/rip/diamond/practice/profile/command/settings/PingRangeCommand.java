package rip.diamond.practice.profile.command.settings;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;
import rip.diamond.practice.util.option.Option;

import java.util.List;
import java.util.stream.Collectors;

public class PingRangeCommand extends Command {
    @CommandArgs(name = "pingrange", aliases = {"pr"}, permission = "eden.settings.ping-range")
    public void execute(CommandArguments command) {
        String[] args = command.getArgs();
        Player player = command.getPlayer();

        if (args.length == 0) {
            Language.INVALID_SYNTAX.sendMessage(player);
            return;
        }

        PlayerProfile profile = PlayerProfile.get(player);

        ProfileSettings settings = ProfileSettings.PING_RANGE;
        List<String> allowedValues = settings.getOptions().subList(1, settings.getOptions().size() - 1).stream().map(Option::getName).collect(Collectors.toList());
        String userOption = args[0].toLowerCase();

        if (!allowedValues.contains(userOption) && !userOption.equals("unlimited")) {
            Language.INVALID_SYNTAX.sendMessage(player);
            return;
        }

        if (userOption.equals("unlimited")) {
            profile.getSettings().replace(settings, settings.getOptions().get(0));
        } else {
            profile.getSettings().replace(settings, settings.getOption(userOption));
        }
        profile.getSettings().get(settings).run(player);
        settings.runSettingsChangeEvent(player, profile);

        Language.PROFILE_SETTINGS_SUCCESSFULLY_CHANGED.sendMessage(player, settings.getName(), profile.getSettings().get(settings).getName());
    }
}
