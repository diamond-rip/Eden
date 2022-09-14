package rip.diamond.practice.match.task;

import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.TaskTicker;

public class MatchPostMatchInventoriesClearTask extends TaskTicker{

    public MatchPostMatchInventoriesClearTask() {
        super(0, 20, true);
    }

    @Override
    public void onRun() {
        Match.getPostMatchInventories().entrySet().removeIf(next -> System.currentTimeMillis() - next.getValue().getCreated() >= 60000L);
    }

    @Override
    public void preRun() {

    }

    @Override
    public TaskTicker.TickType getTickType() {
        return TaskTicker.TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }

}
