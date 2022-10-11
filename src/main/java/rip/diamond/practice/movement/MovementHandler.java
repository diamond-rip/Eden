package rip.diamond.practice.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.hook.spigot.SpigotType;
import rip.diamond.practice.movement.type.DefaultMovementHandler;
import rip.diamond.practice.movement.type.ImanitySpigotMovementHandler;

public abstract class MovementHandler {

    private final Eden plugin = Eden.INSTANCE;

    public MovementHandler() {
        if (plugin.getHookManager().getSpigotController().getSpigotType() == SpigotType.IMANITY_SPIGOT_3) {
            new ImanitySpigotMovementHandler(plugin, this);
        } else {
            new DefaultMovementHandler(plugin, this);
        }
    }

    public abstract void onUpdateLocation(Player player, Location from, Location to);

    public abstract void onUpdateRotation(Player player, Location from, Location to);

}
