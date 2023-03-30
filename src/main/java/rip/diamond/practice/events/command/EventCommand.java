package rip.diamond.practice.events.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.events.menu.EventCreateMenu;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class EventCommand extends Command {
    @CommandArgs(name = "event")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        EdenEvent event = EdenEvent.getOnGoingEvent();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!player.hasPermission("eden.command.event.create")) {
                    Language.NO_PERMISSION.sendMessage(player);
                    return;
                }
                if (event != null) {
                    Language.EVENT_EVENT_IS_RUNNING.sendMessage(player);
                    return;
                }
                new EventCreateMenu().openMenu(player);
                return;
            } else if (args[0].equalsIgnoreCase("forcestart")) {
                if (!player.hasPermission("eden.command.event.forcestart")) {
                    Language.NO_PERMISSION.sendMessage(player);
                    return;
                }
                if (event == null) {
                    Language.EVENT_EVENT_IS_NOT_RUNNING.sendMessage(player);
                    return;
                }
                if (event.getState() != EventState.WAITING) {
                    Language.EVENT_EVENT_ALREADY_STARTED.sendMessage(player);
                    return;
                }
                event.start();
                return;
            } else if (args[0].equalsIgnoreCase("status")) {
                if (event == null) {
                    Language.EVENT_EVENT_IS_NOT_RUNNING.sendMessage(player);
                    return;
                }
                if (event.getStatus(player) == null) {
                    Language.EVENT_NO_AVAILABLE_STATUS.sendMessage(player);
                    return;
                }

                Common.sendMessage(player, event.getStatus(player));
                return;
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (!player.hasPermission("eden.command.event.cancel")) {
                    Language.NO_PERMISSION.sendMessage(player);
                    return;
                }
                if (event == null) {
                    Language.EVENT_EVENT_IS_NOT_RUNNING.sendMessage(player);
                    return;
                }
                event.end(true);
                return;
            }
        }
    }
}
