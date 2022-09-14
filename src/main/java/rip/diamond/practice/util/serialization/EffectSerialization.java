package rip.diamond.practice.util.serialization;


import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class EffectSerialization {

    public static String serializeEffects(Collection<PotionEffect> effects) {
        StringBuilder builder = new StringBuilder();
        for (PotionEffect potionEffect : effects) {
            builder.append(serializePotionEffect(potionEffect));
            builder.append(";");
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
        StringBuilder builder = new StringBuilder();

        if (potionEffect == null) {
            return "null";
        }
        String name = potionEffect.getType().getName();
        builder.append("n@").append(name);

        String duration = String.valueOf(potionEffect.getDuration());
        builder.append(":d@").append(String.valueOf(duration));

        String amplifier = String.valueOf(potionEffect.getAmplifier());
        builder.append(":a@").append(amplifier);

        return builder.toString();
    }

    public static PotionEffect deserializePotionEffect(String source) {
        String name = "";
        String duration = "";
        String amplifier = "";

        if (source.equals("null")) {
            return null;
        }
        String[] split = source.split(":");

        for (String effectInfo : split) {
            String[] itemAttribute = effectInfo.split("@");
            String s2 = itemAttribute[0];

            if (s2.equalsIgnoreCase("n")) {
                name = itemAttribute[1];
            }
            if (s2.equalsIgnoreCase("d")) {
                duration = itemAttribute[1];
            }
            if (s2.equalsIgnoreCase("a")) {
                amplifier = itemAttribute[1];
            }
        }
        return new PotionEffect(PotionEffectType.getByName(name), Integer.parseInt(duration), Integer.parseInt(amplifier));
    }
}
