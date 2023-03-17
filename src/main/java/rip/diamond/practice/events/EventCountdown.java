package rip.diamond.practice.events;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.diamond.practice.Eden;
import rip.diamond.practice.profile.cooldown.Cooldown;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.Arrays;

public abstract class EventCountdown extends Cooldown {

    private final BukkitTask task;

    public EventCountdown(boolean async, int seconds, int... tick) {
        super(seconds);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (isExpired()) {
                    runExpired();
                    cancel();
                    return;
                }
                if (Arrays.stream(tick).anyMatch(i -> i == getSecondsLeft())) {
                    runUnexpired(getSecondsLeft());
                }
            }
        };

        if (async) {
            task = runnable.runTaskTimerAsynchronously(Eden.INSTANCE, 20L, 20L);
        } else {
            task = runnable.runTaskTimer(Eden.INSTANCE, 20L, 20L);
        }
    }

    public abstract void runUnexpired(int tick);

    @Override
    public void runUnexpired() {
        throw new PracticeUnexpectedException("Please use runUnexpired(int) instead of runUnexpired()");
    }

    @Override
    public void runExpired() {
        EdenEvent.getOnGoingEvent().setCountdown(null);
        Tasks.run(EventCountdown.this::run);
    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        task.cancel();
    }
}
