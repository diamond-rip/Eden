package rip.diamond.practice.util.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.HumanEntity;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class Command {

    public Eden plugin;
    private CommandExecutor executor;
    private CommandArgs assigned;

    public Command() {
        this.plugin = Eden.INSTANCE;
        this.register();
    }

    public void register() {
        this.assigned = Arrays.stream(this.getClass().getMethods()).filter(method -> method.getAnnotation(CommandArgs.class) != null).map(method -> method.getAnnotation(CommandArgs.class)).findFirst().orElse(null);

        if (assigned != null) {
            this.executor = new CommandExecutor(assigned.name(), assigned);
            this.plugin.getCommandManager().registerCommand(this, executor);
        }
    }

    public abstract void execute(CommandArguments command);

    public List<String> onTabComplete(CommandArguments command) {
        return new ArrayList<>();
    }

    //This is default tab complete which should return a list of online players
    public List<String> getDefaultTabComplete(CommandArguments command) {
        List<String> completors = new ArrayList<>();

        List<String> values = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());

        String[] args = command.getArgs();

        if (args.length == 0) return new ArrayList<>();

        if (!args[args.length - 1].equalsIgnoreCase("")) {
            values.forEach(value -> {
                if (value.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    completors.add(value);
                }
            });
        } else {
            completors.addAll(values);
        }
        return completors;
    }

    public class CommandExecutor extends BukkitCommand {

        private final boolean inGameOnly;
        private final boolean async;
        private CommandArguments executeArguments;

        public CommandExecutor(String name, CommandArgs assigned) {
            super(name);
            this.setAliases(Arrays.asList(assigned.aliases()));
            this.setPermission(assigned.permission());
            this.inGameOnly = assigned.inGameOnly();
            this.async = assigned.async();
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (this.inGameOnly && sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.RED + "This is for player use only!");
                return false;
            }
            if (this.getPermission().length() > 0 && !sender.hasPermission(this.getPermission())) {
                sender.sendMessage(Language.NO_PERMISSION.toString());
                return false;
            }

            this.executeArguments = new CommandArguments(sender, label, args);

            if (this.async) {
                Tasks.runAsync(() -> Command.this.execute(executeArguments));
            } else {
                Command.this.execute(executeArguments);
            }
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
            List<String> completors = onTabComplete(new CommandArguments(sender, null, args));

            if (completors.isEmpty()) {
                completors.addAll(getDefaultTabComplete(new CommandArguments(sender, null, args)));
            }
            return completors;
        }
    }
}
