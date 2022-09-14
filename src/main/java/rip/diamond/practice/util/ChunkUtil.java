package rip.diamond.practice.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;

import java.lang.reflect.Field;

/**
 * @author GatoGamer
 * Project: NyaChunk
 */

@UtilityClass
public class ChunkUtil {

    public void setChunkSections(Chunk nmsChunk, ChunkSection[] sections) {
        setField("sections", nmsChunk, sections);
        nmsChunk.getWorld().getWorld().refreshChunk(nmsChunk.locX, nmsChunk.locZ);
    }

    public ChunkSection[] copyChunkSections(ChunkSection[] sections) {
        ChunkSection[] newSections = new ChunkSection[sections.length];
        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] == null) continue;
            newSections[i] = copyChunkSection(sections[i]);
        }
        return newSections;
    }

    @SneakyThrows
    public ChunkSection copyChunkSection(ChunkSection chunkSection) {
        ChunkSection section = new ChunkSection(chunkSection.getYPosition(), chunkSection.getSkyLightArray() != null);

        setField("nonEmptyBlockCount", section, getFromField("nonEmptyBlockCount", chunkSection));
        setField("tickingBlockCount", section, getFromField("tickingBlockCount", chunkSection));
        setField("blockIds", section, chunkSection.getIdArray().clone());

        if (chunkSection.getEmittedLightArray() != null) section.a(cloneNibbleArray(chunkSection.getEmittedLightArray()));
        if (chunkSection.getSkyLightArray() != null) section.b(cloneNibbleArray(chunkSection.getSkyLightArray()));

        return section;
    }

    public NibbleArray cloneNibbleArray(NibbleArray nibbleArray) {
        return new NibbleArray(nibbleArray.a().clone());
    }

    @SneakyThrows
    public void setField(String fieldName, Object clazz, Object value) {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(clazz, value);
    }

    @SneakyThrows
    public Object getFromField(String fieldName, Object clazz) {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(clazz);
    }
}
