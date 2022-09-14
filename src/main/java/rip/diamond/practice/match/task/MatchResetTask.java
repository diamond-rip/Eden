package rip.diamond.practice.match.task;

import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;

public class MatchResetTask extends MatchTaskTicker {
    private final Eden plugin = Eden.INSTANCE;
    private final Match match;

    public MatchResetTask(Match match) {
        super(100, 1, false, match);
        this.match = match;
    }

    @Override
    public void onRun() {
        cancel();

        match.clearEntities(true);
        match.getMatchPlayers().forEach(player -> plugin.getLobbyManager().sendToSpawnAndReset(player));
        match.getSpectators().forEach(match::leaveSpectate);
        match.getTasks().forEach(BukkitRunnable::cancel);
        match.getArenaDetail().restoreSnapshot();
        match.getArenaDetail().setUsing(false);
        Match.getMatches().remove(match.getUuid());
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
