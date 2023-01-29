package rip.diamond.practice.misc.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleItemFlagCommand extends Command {
    @CommandArgs(name = "toggleitemflag", permission = "eden.command.toggleitemflag")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null) {
            Common.sendMessage(player, "&cYou must hold an item first!");
            return;
        }
        if (!Checker.isItemFlag(args[0])) {
            Common.sendMessage(player, "&c'" + args[0] + "' is not a valid ItemFlag!");
            return;
        }
        ItemFlag flag = ItemFlag.valueOf(args[0]);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasItemFlag(flag)) {
            meta.removeItemFlags(flag);
        } else {
            meta.addItemFlags(flag);
        }
        itemStack.setItemMeta(meta);
        Common.sendMessage(player, "&aToggled " + flag.name());
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(ItemFlag.values()).map(ItemFlag::name).collect(Collectors.toList());
    }
}
