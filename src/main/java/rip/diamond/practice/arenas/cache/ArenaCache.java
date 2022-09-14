package rip.diamond.practice.arenas.cache;

import net.minecraft.server.v1_8_R3.ChunkSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaCache {

    public Map<ArenaChunk, ChunkSection[]> chunks = new ConcurrentHashMap<>();

    public ChunkSection[] getArenaChunkAtLocation(int x, int z) {
        for (Map.Entry<ArenaChunk, ChunkSection[]> chunksFromMap : this.chunks.entrySet()) {
            if (chunksFromMap.getKey().getX() != x || chunksFromMap.getKey().getZ() != z) continue;
            return chunksFromMap.getValue();
        }
        return null;
    }
}
