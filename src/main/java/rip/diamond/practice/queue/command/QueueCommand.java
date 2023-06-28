package rip.diamond.practice.queue.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueProfile;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.queue.menu.QueueMenu;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueueCommand extends Command {
    @CommandArgs(name = "queue")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        String[] args = command.getArgs();

        if (args.length == 1) {
            if (profile.getPlayerState() == PlayerState.IN_QUEUE && args[0].equalsIgnoreCase("leave")) {
                QueueProfile qProfile = Queue.getPlayers().get(player.getUniqueId());
                if (qProfile == null) {
                    Language.QUEUE_CANNOT_QUIT_QUEUE.sendMessage(player);
                    return;
                }
                Queue.leaveQueue(player);
                return;
            }

            if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
                Language.QUEUE_CANNOT_QUEUE.sendMessage(player);
                return;
            }

            if (Party.getByPlayer(player) != null) {
                Language.PARTY_IN_A_PARTY.sendMessage(player);
                return;
            }

            if (!Checker.isQueueType(args[0])) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            QueueType queueType = QueueType.valueOf(args[0].toUpperCase());
            new QueueMenu(queueType).openMenu(player);
            return;
        } else if (args.length == 2) {
            if (!Checker.isQueueType(args[0])) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }
            QueueType queueType = QueueType.valueOf(args[0].toUpperCase());

            Kit kit = Kit.getByName(args[1]);
            if (kit == null) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }
            if (queueType == QueueType.RANKED && !kit.isRanked()) {
                Language.INVALID_SYNTAX.sendMessage(player);
                return;
            }

            Queue.joinQueue(player, kit, queueType);
            return;
        }

        Language.QUEUE_USAGE.sendMessage(player);
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(QueueType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
