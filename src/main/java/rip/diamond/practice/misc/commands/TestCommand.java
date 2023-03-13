package rip.diamond.practice.misc.commands;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchEntity;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;

public class TestCommand extends Command {
    private static Match match;

    @CommandArgs(name = "test", permission = "eden.command.test")
    public void execute(CommandArguments command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args[0].equalsIgnoreCase("1")) {
            match = PlayerProfile.get(sender).getMatch();
            Common.sendMessage(sender, "done 1");
            return;
        } else if (args[0].equalsIgnoreCase("2")) {
            for (MatchEntity entity : match.getEntities()) {
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntity().getEntityId());
                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(packet);
            }
            Common.sendMessage(sender, "done 2");
        }
    }
}
