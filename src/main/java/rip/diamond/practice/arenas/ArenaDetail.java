package rip.diamond.practice.arenas;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import rip.diamond.practice.arenas.cache.ArenaCache;
import rip.diamond.practice.arenas.cache.ArenaChunk;
import rip.diamond.practice.util.ChunkUtil;
import rip.diamond.practice.util.cuboid.Cuboid;

@Getter
@Setter
public class ArenaDetail {

    private Arena arena;
    private ArenaCache cache;

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

    /**
     * Caches the arena's original chunks and saves them in memory
     * for restoration after the arena is used. By doing this we
     * improve the restoration speed and fix a lot of bugs regarding
     * resetting the arena, this way whatever you do inside the arena
     * chunks, it always gets reset no matter what.
     *
     * @author gatoGamer
     */
    public void takeSnapshot() {
        Cuboid cuboid = new Cuboid(min, max);
        ArenaCache chunkCache = new ArenaCache();
        cuboid.getChunks().forEach(chunk -> {
            chunk.load();
            Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            ChunkSection[] nmsSections = ChunkUtil.copyChunkSections(nmsChunk.getSections());
            chunkCache.chunks.put(new ArenaChunk(chunk.getX(), chunk.getZ()), ChunkUtil.copyChunkSections(nmsSections));
        });

        this.cache = chunkCache;
    }

    /**
     * This method completely restores the arena to its
     * original state, no matter what changes have occurred inside
     * its chunks. This method instead of running a tracker and runnable
     * might be a bit of memory excessive but does better in performance
     * as compared to the latter.
     *
     * @author gatoGamer
     */
    public void restoreSnapshot() {
        Cuboid cuboid = new Cuboid(min, max);
        cuboid.getChunks().forEach(chunk -> {
            try {
                chunk.load();
                Chunk craftChunk = ((CraftChunk)chunk).getHandle();
                ChunkSection[] sections = ChunkUtil.copyChunkSections(this.cache.getArenaChunkAtLocation(chunk.getX(), chunk.getZ()));
                ChunkUtil.setChunkSections(craftChunk, sections);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
