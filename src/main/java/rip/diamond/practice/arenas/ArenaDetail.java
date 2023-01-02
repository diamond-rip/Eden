package rip.diamond.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import rip.diamond.practice.util.cuboid.Cuboid;

@Getter
@Setter
public class ArenaDetail {

    private Arena arena;

    private Location a;
    private Location b;

    private Location min;
    private Location max;

    private boolean using = false;

    public ArenaDetail(Arena arena) {
        this.arena = arena;
    }

    public ArenaDetail(Arena arena, Location a, Location b, Location min, Location max) {
        this.arena = arena;
        this.a = a;
        this.b = b;
        this.min = min;
        this.max = max;
    }

    public boolean isFinishedSetup() {
        return a != null && b != null && min != null && max != null;
    }

    public Cuboid getCuboid() {
        return new Cuboid(min, max);
    }
}
