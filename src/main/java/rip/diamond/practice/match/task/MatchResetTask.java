package rip.diamond.practice.match.task;

import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;

import java.util.Objects;

public class MatchResetTask extends MatchTaskTicker {
    private final Eden plugin = Eden.INSTANCE;
    private final Match match;

    public MatchResetTask(Match match) {
        super(1, 100, false, match);
        this.match = match;
    }

    @Override
    public void onRun() {
        if (getTicks() <= 0) {
            cancel();

            match.clearEntities(true);
            match.getMatchPlayers().stream().filter(player -> Objects.nonNull(player) && player.isOnline()).forEach(player -> plugin.getLobbyManager().sendToSpawnAndReset(player));
            match.getSpectators().forEach(match::leaveSpectate);
            match.getTasks().forEach(BukkitRunnable::cancel);
            match.getArenaDetail().restoreSnapshot();
            match.getArenaDetail().setUsing(false);
            Match.getMatches().remove(match.getUuid());
        }
    }

    @Override
    public void preRun() {
        //Cancel MatchClearBlockTask first, to save performance
        match.getTasks().stream().filter(taskTicker -> taskTicker instanceof MatchClearBlockTask).forEach(BukkitRunnable::cancel);
    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        return 1;
    }
}
