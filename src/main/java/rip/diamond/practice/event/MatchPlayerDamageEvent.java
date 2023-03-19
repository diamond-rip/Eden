package rip.diamond.practice.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
public class MatchPlayerDamageEvent extends BaseEvent implements Cancellable {

    private final Player player;
    private final Match match;
    private final EntityDamageEvent.DamageCause cause;
    @Setter private double damage;

    @Setter private boolean cancelled = false;
    @Setter private boolean ignoreProtection = false;

    public MatchPlayerDamageEvent(Player player, Match match, EntityDamageEvent.DamageCause cause, double damage) {
        this.player = player;
        this.match = match;
        this.cause = cause;
        this.damage = damage;
    }
}
