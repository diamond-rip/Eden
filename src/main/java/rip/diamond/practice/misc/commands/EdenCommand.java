package rip.diamond.practice.misc.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import rip.diamond.practice.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EdenCommand extends Command {

    @CommandArgs(name = "eden")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            Common.sendMessage(player,
                    CC.CHAT_BAR,
                    CC.AQUA + plugin.getDescription().getName() + CC.GRAY + " - " + CC.DARK_AQUA + "v" + plugin.getDescription().getVersion(),
                    CC.WHITE + "Author: " + CC.AQUA + StringUtils.join(plugin.getDescription().getAuthors(), CC.GRAY + ", " + CC.AQUA),
                    CC.WHITE + "Description: " + CC.AQUA + plugin.getDescription().getDescription(),
                    CC.WHITE + "Website: " + CC.AQUA + CC.UNDER_LINE + plugin.getDescription().getWebsite(),
                    CC.CHAT_BAR
            );
            return;
        }

        if (!player.hasPermission("eden.command.eden")) {
            Language.NO_PERMISSION.sendMessage(player);
            return;
        }

        Action action;
        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            Common.sendMessage(player, CC.RED + "Invalid action! Available action: " + Arrays.stream(Action.values()).map(Action::name).collect(Collectors.joining(", ")));
            return;
        }

        switch (action){
            case RELOAD:
                plugin.getArenaFile().load();
                plugin.getConfigFile().load();
                plugin.getItemFile().load();
                plugin.getLanguageFile().load();
                plugin.getLocationFile().load();
                Common.sendMessage(player, CC.GREEN + "Files reloaded!", CC.YELLOW + "Remember: some part of the files might require restart the server to work. And we strongly recommend");
                return;
            case DEBUG:
                plugin.getConfigFile().getConfiguration().set("debug", !plugin.getConfigFile().getBoolean("debug"));
                plugin.getConfigFile().save();
                plugin.getConfigFile().load();
                Common.sendMessage(player, CC.GREEN + "Debug is now: " + (plugin.getConfigFile().getBoolean("debug") ? CC.GREEN + Language.ENABLED.toString() : CC.RED + Language.DISABLED.toString()));
                return;
        }
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(Action.values()).map(Action::name).collect(Collectors.toList());
    }

    enum Action {
        RELOAD, DEBUG
    }
}
