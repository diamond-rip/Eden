package rip.diamond.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.cuboid.Cuboid;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArenaDetail {

    private final Arena arena;
    private final List<ArenaChunk> cachedChunks;

    private Location a;
    private Location b;

    private Location min;
    private Location max;

    private boolean using = false;

    public ArenaDetail(Arena arena) {
        this.arena = arena;
        this.cachedChunks = new ArrayList<>();
    }

    public ArenaDetail(Arena arena, Location a, Location b, Location min, Location max) {
        this.arena = arena;
        this.cachedChunks = new ArrayList<>();
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

    public void copyChunk() {
        Cuboid cuboid = new Cuboid(min, max);
        cuboid.getChunks().forEach(chunk -> cachedChunks.add(new ArenaChunk(chunk)));
    }

    public void restoreChunk() {
        long started = System.currentTimeMillis();
        Common.debug("正在嘗試還原場地...");

        cachedChunks.forEach(ArenaChunk::restore);

        long ended = System.currentTimeMillis();
        Common.debug("還原場地成功! 耗費 " + (ended - started) + "ms");
    }
}
