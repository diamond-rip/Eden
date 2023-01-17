package rip.diamond.practice.arenas.chunk;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;

@Getter
@RequiredArgsConstructor
public class ArenaChunkSection extends Reflection {

    private final boolean setup;

    private int yPos;
    private int nonEmptyBlockCount;
    private int tickingBlockCount;
    private char[] blockIds;
    private NibbleArray emittedLight;
    private NibbleArray skyLight;
    private boolean isDirty; // PaperSpigot

    public ArenaChunkSection(ChunkSection section) {
        if (section == null) {
            this.setup = false;
            return;
        }
        this.yPos = section.getYPosition();
        this.nonEmptyBlockCount = (int) getDeclaredField(section, "nonEmptyBlockCount");
        this.tickingBlockCount = (int) getDeclaredField(section, "tickingBlockCount");
        this.blockIds = section.getIdArray().clone();
        this.emittedLight = clone(section.getEmittedLightArray());
        this.skyLight = clone(section.getSkyLightArray());
        this.isDirty = (boolean) getDeclaredField(section, "isDirty");

        this.setup = true;
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

    public ChunkSection toChunkSection() {
        if (setup) {
            ChunkSection section = new ChunkSection(yPos, getSkyLight() != null);

            setDeclaredField(section, "yPos", yPos);
            setDeclaredField(section, "nonEmptyBlockCount", nonEmptyBlockCount);
            setDeclaredField(section, "tickingBlockCount", tickingBlockCount);
            setDeclaredField(section, "blockIds", getBlockIds());
            setDeclaredField(section, "emittedLight", getEmittedLight());
            setDeclaredField(section, "skyLight", getSkyLight());
            setDeclaredField(section, "isDirty", isDirty);

            return section;
        }
        return null;
    }
}
