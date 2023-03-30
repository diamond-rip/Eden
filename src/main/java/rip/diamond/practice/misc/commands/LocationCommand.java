package rip.diamond.practice.misc.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;
import rip.diamond.practice.util.serialization.LocationSerialization;

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

            Location location = player.getLocation();
            switch (type) {
                case SPAWN:
                    plugin.getLobbyManager().setSpawnLocation(location);
                    plugin.getLocationFile().getConfiguration().set("spawn-location", LocationSerialization.serializeLocation(location));
                    plugin.getLocationFile().save();
                    break;
                case EDITOR:
                    plugin.getKitEditorManager().setEditorLocation(location);
                    plugin.getLocationFile().getConfiguration().set("editor-location", LocationSerialization.serializeLocation(location));
                    plugin.getLocationFile().save();
                    break;
                case SUMO_EVENT_A:
                    plugin.getLocationFile().getConfiguration().set("sumo-event.a", LocationSerialization.serializeLocation(location));
                    plugin.getLocationFile().save();
                    break;
                case SUMO_EVENT_B:
                    plugin.getLocationFile().getConfiguration().set("sumo-event.b", LocationSerialization.serializeLocation(location));
                    plugin.getLocationFile().save();
                    break;
                case SUMO_EVENT_SPECTATOR:
                    plugin.getLocationFile().getConfiguration().set("sumo-event.spectator", LocationSerialization.serializeLocation(location));
                    plugin.getLocationFile().save();
                    break;
                default:
                    return;
            }
            Language.LOCATION_CHANGED.sendMessage(player, type.name());
        }
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(LocationType.values()).map(LocationType::name).collect(Collectors.toList());
    }

    enum LocationType {
        SPAWN,
        EDITOR,
        SUMO_EVENT_A,
        SUMO_EVENT_B,
        SUMO_EVENT_SPECTATOR
    }
}
