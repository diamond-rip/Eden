package rip.diamond.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//Credit: https://gist.github.com/aaron1998ish/1dd3f183d8d55902f65ea187019058ac

public class FireballUtil {

    private static Field fieldFireballDirX;
    private static Field fieldFireballDirY;
    private static Field fieldFireballDirZ;

    private static Method craftFireballHandle;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String nmsFireball = "net.minecraft.server." + version + "EntityFireball";
        String craftFireball = "org.bukkit.craftbukkit." + version + "entity.CraftFireball";
        try {
            Class<?> fireballClass = Class.forName(nmsFireball);

            //should be accessible by default.
            fieldFireballDirX = fireballClass.getDeclaredField("dirX");
            fieldFireballDirY = fireballClass.getDeclaredField("dirY");
            fieldFireballDirZ = fireballClass.getDeclaredField("dirZ");

            craftFireballHandle = Class.forName(craftFireball).getDeclaredMethod("getHandle");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static Fireball setDirection(Fireball fireball, Vector direction) {
        double speed = Config.MATCH_FIREBALL_SPEED.toDouble();

        try {
            Object handle = craftFireballHandle.invoke(fireball);
            fieldFireballDirX.set(handle, direction.getX() * 0.10D * speed);
            fieldFireballDirY.set(handle, direction.getY() * 0.10D * speed);
            fieldFireballDirZ.set(handle, direction.getZ() * 0.10D * speed);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return fireball;
    }


}
