package rip.diamond.practice.arenas.chunk;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

public class NewArenaChunk extends Reflection implements IArenaChunk {

    private final World world;
    @Getter private final int x, z;
    private final ChunkSection[] data;

    public NewArenaChunk(Chunk chunk) {
        chunk.load();

        ChunkSection[] sections = ((CraftChunk) chunk).getHandle().getSections();

        this.world = chunk.getWorld();
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.data = new ChunkSection[sections.length];

        for (int i = 0; i < sections.length; i++) {
            ChunkSection original = sections[i];
            if (original != null) {
                data[i] = cloneSection(original);
            }
        }
    }

    @Override
    public void restore() {
        Chunk bukkitChunk = world.getChunkAt(x,z);
        net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) bukkitChunk).getHandle();

        bukkitChunk.load();
        ChunkSection[] sections = new ChunkSection[data.length];
        for (int i = 0; i < data.length; i++) {
            ChunkSection prepareToClone = data[i];
            if (prepareToClone != null) {
                sections[i] = cloneSection(prepareToClone);
            }
        }
        setDeclaredField(nmsChunk, "sections", sections);
        nmsChunk.getWorld().getWorld().refreshChunk(x, z);
    }

    private ChunkSection cloneSection(ChunkSection original) {
        ChunkSection section = new ChunkSection(original.getYPosition(), original.getSkyLightArray() != null);

        setDeclaredField(section, "nonEmptyBlockCount", getDeclaredField(original, "nonEmptyBlockCount"));
        setDeclaredField(section, "tickingBlockCount", getDeclaredField(original, "tickingBlockCount"));
        setDeclaredField(section, "blockIds", original.getIdArray().clone());
        if (original.getEmittedLightArray() != null) {
            section.a(new NibbleArray(original.getEmittedLightArray().a().clone()));
        }
        if (original.getSkyLightArray() != null) {
            section.b(new NibbleArray(original.getSkyLightArray().a().clone()));
        }
        return section;
    }

}
