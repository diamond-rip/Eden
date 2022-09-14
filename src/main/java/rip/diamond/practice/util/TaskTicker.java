package rip.diamond.practice.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;

public abstract class TaskTicker extends BukkitRunnable {

    @Getter
    @Setter
    private int ticks;
    private boolean finishPreRun = false;

    public TaskTicker(int delay, int period, boolean async) {
        if (async) {
            this.runTaskTimerAsynchronously(Eden.INSTANCE, delay, period);
        } else {
            this.runTaskTimer(Eden.INSTANCE, delay, period);
        }
    }

    @Override
    public void run() {
        if (!finishPreRun) {
            ticks = getStartTick();
            preRun();
            finishPreRun = true;
        }
        onRun();
        if (getTickType() == TickType.COUNT_UP) {
            countUp();
        } else if (getTickType() == TickType.COUNT_DOWN) {
            countDown();
        }
    }

    public abstract void onRun();

    public void preRun() {

    }

    public abstract TickType getTickType();

    public abstract int getStartTick();

    public void countUp() {
        ticks++;
    }

    public void countDown() {
        ticks--;
    }

    public enum TickType {
        COUNT_UP,
        COUNT_DOWN,
        NONE
    }

}