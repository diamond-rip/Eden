package rip.diamond.practice.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.scheduler.BukkitScheduler;
import rip.diamond.practice.Eden;

import java.util.concurrent.ThreadFactory;

public class Tasks {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Runnable runnable, boolean async) {
        if(async) {
            Eden.INSTANCE.getServer().getScheduler().runTaskAsynchronously(Eden.INSTANCE, runnable);
        } else {
            runnable.run();
        }
    }

    public static void run(Runnable runnable) {
        Eden.INSTANCE.getServer().getScheduler().runTask(Eden.INSTANCE, runnable);
    }

    public static void runAsync(Runnable runnable) {
        Eden.INSTANCE.getServer().getScheduler().runTaskAsynchronously(Eden.INSTANCE, runnable);
    }

    public static void runLater(Runnable runnable, long delay) {
        Eden.INSTANCE.getServer().getScheduler().runTaskLater(Eden.INSTANCE, runnable, delay);
    }

    public static void runAsyncLater(Runnable runnable, long delay) {
        Eden.INSTANCE.getServer().getScheduler().runTaskLaterAsynchronously(Eden.INSTANCE, runnable, delay);
    }

    public static void runTimer(Runnable runnable, long delay, long interval) {
        Eden.INSTANCE.getServer().getScheduler().runTaskTimer(Eden.INSTANCE, runnable, delay, interval);
    }

    public static void runAsyncTimer(Runnable runnable, long delay, long interval) {
        Eden.INSTANCE.getServer().getScheduler().runTaskTimerAsynchronously(Eden.INSTANCE, runnable, delay, interval);
    }

    public static BukkitScheduler getScheduler() {
        return Eden.INSTANCE.getServer().getScheduler();
    }
}
