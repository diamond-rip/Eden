package rip.diamond.practice.match;

import lombok.Getter;
import org.bukkit.entity.Entity;
import rip.diamond.practice.util.Common;

@Getter
public class MatchEntity {

    private final long timestamp;
    private final Match match;
    private final Entity entity;

    public MatchEntity(Match match, Entity entity) {
        this.timestamp = System.currentTimeMillis();
        this.match = match;
        this.entity = entity;
    }

}
