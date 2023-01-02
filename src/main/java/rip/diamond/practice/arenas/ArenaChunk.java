package rip.diamond.practice.arenas;

import io.github.epicgo.sconey.reflection.Reflection;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import rip.diamond.practice.arenas.chunk.ArenaChunkSection;

import java.util.HashMap;
import java.util.Map;

public class ArenaChunk extends Reflection {

    private final World world;
    private final int x, z;
    private final Map<ChunkSection, ArenaChunkSection> data;

    public ArenaChunk(Chunk chunk) {
        chunk.load();

        this.world = chunk.getWorld();
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.data = new HashMap<>();

        for (ChunkSection section : ((CraftChunk) chunk).getHandle().getSections()) {
            if (section == null) {
                continue;
            }
            data.put(section, new ArenaChunkSection(section));
        }
    }

    public void restore() {
        data.forEach((chunkSection, arenaChunkSection) -> {
            ArenaChunkSection section = arenaChunkSection.clone();

            setDeclaredField(chunkSection, "yPos", section.yPos);
            setDeclaredField(chunkSection, "nonEmptyBlockCount", section.nonEmptyBlockCount);
            setDeclaredField(chunkSection, "tickingBlockCount", section.tickingBlockCount);
            setDeclaredField(chunkSection, "blockIds", section.blockIds);
            setDeclaredField(chunkSection, "emittedLight", section.emittedLight);
            setDeclaredField(chunkSection, "skyLight", section.skyLight);
            setDeclaredField(chunkSection, "isDirty", section.isDirty);
        });

        world.refreshChunk(x, z);
    }

}
