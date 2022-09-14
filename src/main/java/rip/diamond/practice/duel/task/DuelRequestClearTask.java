package rip.diamond.practice.duel.task;

import rip.diamond.practice.duel.DuelRequest;
import rip.diamond.practice.util.TaskTicker;

public class DuelRequestClearTask extends TaskTicker {
    public DuelRequestClearTask() {
        super(0, 20, true);
    }

    @Override
    public void onRun() {
        DuelRequest.getDuelRequests().entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    @Override
    public void preRun() {

    }

    @Override
    public TickType getTickType() {
        return TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }
}
