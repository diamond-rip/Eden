package rip.diamond.practice.events;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.Cooldown;
import rip.diamond.practice.util.Tasks;

import java.util.Arrays;

public class EventCountdown extends Cooldown {

    private final BukkitTask task;

    public EventCountdown(int seconds, int... tick) {
        super(seconds);

        // TODO: 23/9/2022 Tick count is not match scoreboard
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (isExpired()) {
                    EdenEvent.getOnGoingEvent().setCountdown(null);
                    Tasks.run(EventCountdown.this::run);
                    cancel();
                    return;
                }
                if (Arrays.stream(tick).anyMatch(i -> i == getSecondsLeft())) {
                    runTick(getSecondsLeft());
                }
            }
        }.runTaskTimerAsynchronously(Eden.INSTANCE, 20L, 20L);
    }

    public void runTick(int tick) {

    }

    @Override
    public void cancelCountdown() {
        super.cancelCountdown();
        task.cancel();
    }
}
