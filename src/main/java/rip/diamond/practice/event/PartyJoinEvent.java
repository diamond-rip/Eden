package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.BaseEvent;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

@Getter
@RequiredArgsConstructor
public class PartyJoinEvent extends BaseEvent implements Cancellable {

    private final Party party;
    private final boolean forced;

    private boolean cancelled;
    private String cancelReason;

    @Override
    @Deprecated
    public void setCancelled(boolean cancelled) {
        throw new PracticeUnexpectedException("Cancel party join must provide a reason.");
    }

    public void setCancelled(boolean cancelled, String reason) {
        this.cancelled = cancelled;
        this.cancelReason = reason;
    }
}
