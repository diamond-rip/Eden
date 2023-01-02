package rip.diamond.practice.arenas.chunk;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;

@RequiredArgsConstructor
public class ArenaChunkSection extends Reflection {

    public final int yPos;
    public final int nonEmptyBlockCount;
    public final int tickingBlockCount;
    public final char[] blockIds;
    public final NibbleArray emittedLight;
    public final NibbleArray skyLight;
    public final boolean isDirty; // PaperSpigot

    public ArenaChunkSection(ChunkSection section) {
        this.yPos = section.getYPosition();
        this.nonEmptyBlockCount = (int) getDeclaredField(section, "nonEmptyBlockCount");
        this.tickingBlockCount = (int) getDeclaredField(section, "tickingBlockCount");
        this.blockIds = section.getIdArray().clone();
        this.emittedLight = clone(section.getEmittedLightArray());
        this.skyLight = clone(section.getSkyLightArray());
        this.isDirty = (boolean) getDeclaredField(section, "isDirty");
    }

    private NibbleArray clone(NibbleArray array) {
        return new NibbleArray(array.a().clone());
    }

    public ArenaChunkSection clone() {
        return new ArenaChunkSection(yPos, nonEmptyBlockCount, tickingBlockCount, blockIds.clone(), clone(emittedLight), clone(skyLight), isDirty);
    }
}
