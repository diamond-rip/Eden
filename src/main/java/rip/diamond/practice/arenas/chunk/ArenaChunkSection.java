package rip.diamond.practice.arenas.chunk;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;

@Getter
@RequiredArgsConstructor
public class ArenaChunkSection extends Reflection {

    private final int yPos;
    private final int nonEmptyBlockCount;
    private final int tickingBlockCount;
    private final char[] blockIds;
    private final NibbleArray emittedLight;
    private final NibbleArray skyLight;
    private final boolean isDirty; // PaperSpigot

    public ArenaChunkSection(ChunkSection section) {
        this.yPos = section.getYPosition();
        this.nonEmptyBlockCount = (int) getDeclaredField(section, "nonEmptyBlockCount");
        this.tickingBlockCount = (int) getDeclaredField(section, "tickingBlockCount");
        this.blockIds = section.getIdArray().clone();
        this.emittedLight = clone(section.getEmittedLightArray());
        this.skyLight = clone(section.getSkyLightArray());
        this.isDirty = (boolean) getDeclaredField(section, "isDirty");
    }

    public char[] getBlockIds() {
        return blockIds.clone();
    }

    public NibbleArray getEmittedLight() {
        return clone(emittedLight);
    }

    public NibbleArray getSkyLight() {
        return clone(skyLight);
    }

    private NibbleArray clone(NibbleArray array) {
        return new NibbleArray(array.a().clone());
    }

    public ArenaChunkSection clone() {
        return new ArenaChunkSection(yPos, nonEmptyBlockCount, tickingBlockCount, blockIds.clone(), clone(emittedLight), clone(skyLight), isDirty);
    }
}
