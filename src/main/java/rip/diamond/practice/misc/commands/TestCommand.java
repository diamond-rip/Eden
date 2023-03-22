package rip.diamond.practice.misc.commands;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchEntity;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;

public class TestCommand extends Command {

    @CommandArgs(name = "test", permission = "eden.command.test")
    public void execute(CommandArguments command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args[0].equalsIgnoreCase("1")) {
            EdenEvent.getOnGoingEvent().countdown(2);
            Common.sendMessage(sender, "done 1");
            return;
        } else if (args[0].equalsIgnoreCase("2")) {
            Eden.INSTANCE.getLeaderboardManager().getBestWinstreakLeaderboard().get(Kit.getByName("boxing")).getLeaderboard().forEach((integer, leaderboardPlayerCache) -> {
                Common.sendMessage(sender, integer + ": " + leaderboardPlayerCache.getPlayerName());
            });
        }


    }
}
