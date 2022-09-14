package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class KitLoadoutReceivedEvent extends BaseEvent {

    private final Player player;
    private final Match match;
    private final KitLoadout kitLoadout;

}
