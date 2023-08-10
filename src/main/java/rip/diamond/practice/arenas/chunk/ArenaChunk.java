package rip.diamond.practice.arenas.chunk;

import com.boydti.fawe.FaweAPI;
import io.github.epicgo.sconey.reflection.Reflection;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

public class ArenaChunk extends Reflection implements IArenaChunk {

    private final World world;
    @Getter private final int x, z;
    private final ArenaChunkSection[] data;

    public ArenaChunk(Chunk chunk) {
        chunk.load();

        ChunkSection[] sections = ((CraftChunk) chunk).getHandle().getSections();

        this.world = chunk.getWorld();
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.data = new ArenaChunkSection[sections.length];

        for (int i = 0; i < sections.length; i++) {
            data[i] = new ArenaChunkSection(sections[i]);
        }
    }

    @Override
    public void restore() {
        //Common.debug("正在還原 chunk (" + world.getName() + ":" + x + ":" + z + ")");

        CraftWorld craftWorld = (CraftWorld) world;

        craftWorld.getChunkAt(x, z).load();
        ChunkSection[] sections = new ChunkSection[data.length];
        for (int i = 0; i < data.length; i++) {
            sections[i] = data[i].toChunkSection();
        }
        setDeclaredField(((CraftChunk) world.getChunkAt(x,z)).getHandle(), "sections", sections);

        boolean success = craftWorld.refreshChunk(x, z);
        if (!success) {
            throw new PracticeUnexpectedException("Cannot refresh chunk (" + x + ":" + z + ")");
        }
    }

}
