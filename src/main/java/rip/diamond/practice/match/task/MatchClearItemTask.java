package rip.diamond.practice.match.task;

import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.TaskTicker;

public class MatchClearItemTask extends TaskTicker {
    public MatchClearItemTask() {
        super(0, 20, false);
    }

    @Override
    public void onRun() {
        for (Match match : Match.getMatches().values()) {
            match.clearEntities(false);
        }
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
