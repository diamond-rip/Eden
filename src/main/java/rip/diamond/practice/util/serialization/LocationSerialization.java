package rip.diamond.practice.util.serialization;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;

public class LocationSerialization {

    public static Location deserializeLocation(String input) {
        if (input == null || input.equals("null")) {
            return null;
        }
        String[] attributes = input.split(":");

        World world = null;
        Double x = null;
        Double y = null;
        Double z = null;
        Float pitch = null;
        Float yaw = null;

        for (String attribute : attributes) {
            String[] split = attribute.split(";");

            if (split[0].equalsIgnoreCase("#w")) {
                if (Bukkit.getWorld(split[1]) == null) {
                    world = Bukkit.createWorld(new WorldCreator(split[1]));
                } else {
                    world = Bukkit.getWorld(split[1]);
                }
                continue;
            }

            if (split[0].equalsIgnoreCase("#x")) {
                x = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#y")) {
                y = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#z")) {
                z = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#p")) {
                pitch = Float.parseFloat(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("#yaw")) {
                yaw = Float.parseFloat(split[1]);
            }
        }

        if (world == null || x == null || y == null || z == null || pitch == null || yaw == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String serializeLocation(Location location) {
        if (location == null) {
            return "null";
        }
        return "#w;" + location.getWorld().getName() +
                ":#x;" + location.getX() +
                ":#y;" + location.getY() +
                ":#z;" + location.getZ() +
                ":#p;" + location.getPitch() +
                ":#yaw;" + location.getYaw();
    }

    public static String toReadable(Location location) {
        if (location == null) {
            return Language.LOCATION_NOT_FOUND.toString();
        }
        return CC.AQUA + "X: " + CC.GREEN + Eden.DECIMAL.format(location.getX()) + CC.AQUA + " Y: " + CC.GREEN + Eden.DECIMAL.format(location.getY()) + CC.AQUA + " Z: " + CC.GREEN + Eden.DECIMAL.format(location.getZ()) + CC.AQUA + " Pitch: " + CC.GREEN + Eden.DECIMAL.format(location.getPitch()) + CC.AQUA + " Yaw: " + CC.GREEN + Eden.DECIMAL.format(location.getYaw());
    }
}
