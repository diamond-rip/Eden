package rip.diamond.practice.match;

import rip.diamond.practice.util.TaskTicker;

public abstract class MatchTaskTicker extends TaskTicker {

    private final Match match;

    public MatchTaskTicker(int delay, int period, boolean async, Match match) {
        super(delay, period, async);

        this.match = match;

        match.getTasks().add(this);
    }

    @Override
    public abstract void onRun();

    @Override
    public abstract void preRun();

    @Override
    public abstract TickType getTickType();

    @Override
    public abstract int getStartTick();

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        if (match.getState() == MatchState.FIGHTING) {
            match.getTasks().remove(this);
        }
    }
}
