package rip.diamond.practice.debug;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class TestCommand extends Command {

    @CommandArgs(name = "test", permission = "eden.command.test", async = true)
    public void execute(CommandArguments command) {
        Player sender = command.getPlayer();
        String[] args = command.getArgs();

        if (args[0].equalsIgnoreCase("1")) {
            sender.setFoodLevel(10);
            return;
        } else if (args[0].equalsIgnoreCase("2")) {
            Bukkit.getPlayer("GoodestEnglish").performCommand("party create");
            Bukkit.getPlayer("Fauzh").performCommand("party join GoodestEnglish");
            Bukkit.getPlayer("DragonL").performCommand("party join GoodestEnglish");
            return;
        }


    }
}
