package rip.diamond.practice.lobby.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocationCommand extends Command {
    @CommandArgs(name = "location", permission = "eden.command.location")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            Common.sendMessage(player, CC.RED + "Usage: /" + command.getLabel() + " <type>");
            return;
        } else if (args.length == 1) {
            LocationType type;
            try {
                type = LocationType.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                Common.sendMessage(player, CC.RED + "Invalid location type! Available type: " + Arrays.stream(LocationType.values()).map(LocationType::name).collect(Collectors.joining(", ")));
                return;
            }

            switch (type) {
                case SPAWN:
                    plugin.getLobbyManager().setSpawnLocation(player);
                    return;
                case EDITOR:
                    plugin.getKitEditorManager().setEditorLocation(player);
                    return;
                default:
                    return;
            }
        }
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(LocationType.values()).map(LocationType::name).collect(Collectors.toList());
    }

    enum LocationType {
        SPAWN,
        EDITOR
    }
}
