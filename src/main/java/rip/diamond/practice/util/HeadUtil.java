package rip.diamond.practice.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;

public class HeadUtil {

    public static String getValue(Player player) {
        if (player != null) {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            GameProfile gameProfile = entityPlayer.getProfile();

            for (Map.Entry<String, Property> entry : gameProfile.getProperties().entries()) {
                return entry.getValue().getValue();
            }
        }

        return null;
    }

    public static String[] getValues(Player player) {
        return getValues(null, player);
    }

    public static String[] getValues(String key, Player player) {
        if (player != null) {
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            GameProfile gameProfile = entityPlayer.getProfile();

            for (Map.Entry<String, Property> entry : gameProfile.getProperties().entries()) {
                if (key == null || entry.getKey().equals(key)) {
                    return new String[]{entry.getValue().getValue(), entry.getValue().getSignature()};
                }
            }
        }

        return null;
    }

}
