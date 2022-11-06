package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import rip.diamond.practice.queue.QueueProfile;
import rip.diamond.practice.util.BaseEvent;

@Getter
@Setter
@RequiredArgsConstructor
public class QueueMatchFoundEvent extends BaseEvent implements Cancellable {

    private final Player playerA;
    private final Player playerB;
    private final QueueProfile queueProfileA;
    private final QueueProfile queueProfileB;

    private boolean cancelled = false;
}
