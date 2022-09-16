package rip.diamond.practice.kits.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.menu.KitDetailsMenu;
import rip.diamond.practice.kits.menu.KitsManagementMenu;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.List;
import java.util.stream.Collectors;

public class KitCommand extends Command {
    @CommandArgs(name = "kit", permission = "eden.command.kit")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            if (Kit.getKits().size() == 0) {
                Language.KIT_NO_KITS_FOUND.sendMessage(player);
                return;
            }
            new KitsManagementMenu().openMenu(player);
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                new KitsManagementMenu().openMenu(player);
                return;
            } else if (args[0].equalsIgnoreCase("saveall")) {
                Kit.getKits().forEach(kit -> kit.save(true));
                Language.KIT_SAVED_ALL_KITS.sendMessage(player);
                return;
            }
            Kit kit = Kit.getByName(args[0]);
            if (kit == null) {
                Language.KIT_NOT_EXISTS.sendMessage(player, args[0]);
                return;
            }
            new KitDetailsMenu(kit, null).openMenu(player);
        } else if (args.length == 2) {
            Kit kit = Kit.getByName(args[1]);
            if (args[0].equalsIgnoreCase("create")) {
                if (kit != null) {
                    Language.KIT_KIT_ALREADY_EXISTS.sendMessage(player, args[1]);
                    return;
                }
                Kit newKit = new Kit(args[1]);
                Kit.getKits().add(newKit);
                Language.KIT_SUCCESSFULLY_CREATE.sendMessage(player, newKit.getName());
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (kit == null) {
                    Language.KIT_NOT_EXISTS.sendMessage(player, args[0]);
                    return;
                }
                kit.delete(true);
                Language.KIT_SUCCESSFULLY_DELETE.sendMessage(player, kit.getName());
            } else if (args[0].equalsIgnoreCase("save")) {
                if (kit == null) {
                    Language.KIT_NOT_EXISTS.sendMessage(player, args[0]);
                    return;
                }
                kit.save(true);
                Language.KIT_SAVED.sendMessage(player, kit.getName());
            }
        }
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Kit.getKits().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
