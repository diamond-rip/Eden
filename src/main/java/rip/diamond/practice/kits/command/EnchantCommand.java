package rip.diamond.practice.kits.command;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class EnchantCommand extends Command {
    @CommandArgs(name = "enchant", aliases = {"forceenchant"}, permission = "eden.command.enchant")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 2) {
            Language.INVALID_SYNTAX.sendMessage(player);
            return;
        }
        if (!Checker.isEnchantment(args[0]) || !Checker.isInteger(args[1])) {
            Language.INVALID_SYNTAX.sendMessage(player);
            return;
        }

        player.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
        Common.sendMessage(player, CC.YELLOW + "[Eden] Successfully enchanted your item.");
        return;
    }
}
