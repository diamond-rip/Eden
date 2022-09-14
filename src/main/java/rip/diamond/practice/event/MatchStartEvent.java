package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@RequiredArgsConstructor
public class MatchStartEvent extends BaseEvent implements Cancellable {

    @Getter private final Match match;

    private boolean cancel = false;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
