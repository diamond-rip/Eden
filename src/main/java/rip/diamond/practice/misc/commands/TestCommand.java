package rip.diamond.practice.misc.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;

public class TestCommand extends Command {
    @CommandArgs(name = "test", permission = "eden.command.test", inGameOnly = false)
    public void execute(CommandArguments command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args[0].equalsIgnoreCase("1")) {
            ((Player) sender).setVelocity(new Vector(Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3])));
            return;
        }

        ((Player) sender).teleport(Bukkit.getPlayer(args[0]));
        ((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 100));
    }

    private String buildMessage(String[] args, int start) {
        if (start >= args.length) {
            return "";
        }
        return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
}
