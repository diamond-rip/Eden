package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
@Setter
@RequiredArgsConstructor
public class MatchPlayerDeathEvent extends BaseEvent {

    private final Match match;
    private final Player player;
    private boolean playLightningEffect = true;
    private boolean playDeathEffect = true;

}
