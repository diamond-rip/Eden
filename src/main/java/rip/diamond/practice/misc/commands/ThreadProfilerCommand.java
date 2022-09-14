package rip.diamond.practice.misc.commands;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.*;
import java.util.stream.Collectors;

public class ThreadProfilerCommand extends Command {

    //Credit: MineHQ's bridge

    @CommandArgs(name = "threadprofiler", aliases = "tpr", permission = "eden.command.threadprofiler")
    public void execute(CommandArguments command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Action action;
        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            Common.sendMessage(sender, CC.RED + "Invalid action! Available action: " + Arrays.stream(Action.values()).map(Action::name).collect(Collectors.joining(", ")));
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        switch (action) {
            case LIST:
                for (Thread thread : threads) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes
                            ('&', " &6* &e"
                                    + thread.getName() + " &d(State: "
                                    + (thread.getState()) + ", Priority: "
                                    + thread.getPriority() + ")"));
                }
                break;
            case GC:
                sender.sendMessage(ChatColor.YELLOW + "Trying to run Java garbage collector to free up memory.");
                long before = System.currentTimeMillis();
                runtime.gc();
                long after = System.currentTimeMillis();
                sender.sendMessage(ChatColor.GOLD
                        + "* "
                        + ChatColor.YELLOW + "Finished! Took "
                        + ChatColor.LIGHT_PURPLE
                        + (after - before)
                        + "ms");
                break;
            case THREADS:
                HashMap<Plugin, Integer> pending = Maps.newHashMap();
                for (BukkitTask worker : Bukkit.getScheduler().getPendingTasks()) {
                    pending.put(worker.getOwner(), pending.getOrDefault(worker.getOwner(), 0) + 1);
                }
                sender.sendMessage(ChatColor.GOLD + "Alive Threads: " + ChatColor.LIGHT_PURPLE + Thread.getAllStackTraces().keySet().parallelStream().filter(Thread::isAlive).count());
                sender.sendMessage(ChatColor.GOLD + "Daemon Threads: " + ChatColor.LIGHT_PURPLE + Thread.getAllStackTraces().keySet().parallelStream().filter(Thread::isDaemon).count());
                sender.sendMessage(ChatColor.GOLD + "Interrupted Threads: " + ChatColor.LIGHT_PURPLE + Thread.getAllStackTraces().keySet().parallelStream().filter(Thread::isInterrupted).count());
                sender.sendMessage(ChatColor.GOLD + "Active Workers: " + ChatColor.LIGHT_PURPLE + Bukkit.getScheduler().getActiveWorkers().size());
                sender.sendMessage(ChatColor.GOLD + "Pending Tasks: " + ChatColor.LIGHT_PURPLE + Bukkit.getScheduler().getPendingTasks().size());
                sender.sendMessage(ChatColor.GOLD + "Threads: " + ChatColor.YELLOW + "(" + threads.size() + " Active) (Ram " + ChatColor.LIGHT_PURPLE + format(runtime.freeMemory()) + " free out of " + ChatColor.LIGHT_PURPLE + format(runtime.maxMemory()) + " " + ChatColor.LIGHT_PURPLE + format(runtime.maxMemory() - runtime.freeMemory()) + " used" + ChatColor.GOLD + ")");
                sender.sendMessage("");
                pending.keySet().stream().sorted((o1, o2) -> pending.get(o2) - pending.get(o1)).forEachOrdered(plugin -> sender.sendMessage(ChatColor.RED + "* " + ChatColor.YELLOW + plugin.getName() + ": " + ChatColor.WHITE + pending.get(plugin)));
                break;
            default:
                break;
        }
    }

    @Override
    public List<String> getDefaultTabComplete(CommandArguments command) {
        return Arrays.stream(Action.values()).map(Action::name).collect(Collectors.toList());
    }

    private String format(double mem) {
        return Math.round(mem / 1024 / 1024) + "MB";
    }

    enum Action {
        LIST,
        GC,
        THREADS
    }
}
