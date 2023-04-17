package rip.diamond.practice.util.serialization;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class EffectSerialization {

    private static final int INITIAL_CAPACITY = 128;

    public static String serializeEffects(Collection<PotionEffect> effects) {
        StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);
        for (PotionEffect potionEffect : effects) {
            builder.append(serializePotionEffect(potionEffect)).append(";");
        }
        return builder.toString();
    }

    public static Collection<PotionEffect> deserializeEffects(String source) {
        if (!source.contains(":")) {
            return new ArrayList<>();
        }
        Collection<PotionEffect> effects = new ArrayList<>();
        String[] split = source.split(";");
        for (String piece : split) {
            effects.add(deserializePotionEffect(piece));
        }
        return effects;
    }

    public static String serializePotionEffect(PotionEffect potionEffect) {
        StringBuilder builder = new StringBuilder(INITIAL_CAPACITY);
        String name = potionEffect == null ? "null" : potionEffect.getType().getName();
        String duration = potionEffect == null ? "0" : String.valueOf(potionEffect.getDuration());
        String amplifier = potionEffect == null ? "0" : String.valueOf(potionEffect.getAmplifier());

        builder.append("n@").append(name).append(":d@").append(duration).append(":a@").append(amplifier);

        return builder.toString();
    }

    public static PotionEffect deserializePotionEffect(String source) {
        if (source.equals("null")) {
            return null;
        }
        String[] split = source.split(":");
        String name = "", duration = "", amplifier = "";
        for (String effectInfo : split) {
            String[] itemAttribute = effectInfo.split("@");
            String s2 = itemAttribute[0];
            if (s2.equalsIgnoreCase("n")) {
                name = itemAttribute[1];
            } else if (s2.equalsIgnoreCase("d")) {
                duration = itemAttribute[1];
            } else if (s2.equalsIgnoreCase("a")) {
                amplifier = itemAttribute[1];
            }
        }
        return new PotionEffect(PotionEffectType.getByName(name), Integer.parseInt(duration), Integer.parseInt(amplifier));
    }
}
