package rip.diamond.practice.match;

import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
public class MatchEntity {

    private final long timestamp;
    private final Entity entity;

    public MatchEntity(Entity entity) {
        this.timestamp = System.currentTimeMillis();
        this.entity = entity;
    }

}
