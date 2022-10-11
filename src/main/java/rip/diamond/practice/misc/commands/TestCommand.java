package rip.diamond.practice.misc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;

public class TestCommand extends Command {
    @CommandArgs(name = "test", permission = "eden.command.test", inGameOnly = false)
    public void execute(CommandArguments command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        //Throw an error
        ((Player) sender).setLevel(10);
    }

    private String buildMessage(String[] args, int start) {
        if (start >= args.length) {
            return "";
        }
        return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }
}
