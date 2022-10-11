package rip.diamond.practice.movement.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.imanity.imanityspigot.movement.MovementHandler;
import org.imanity.imanityspigot.packet.wrappers.MovementPacketWrapper;
import rip.diamond.practice.Eden;

public class ImanitySpigotMovementHandler implements MovementHandler {

    private final rip.diamond.practice.movement.MovementHandler movementHandler;

    public ImanitySpigotMovementHandler(Eden plugin, rip.diamond.practice.movement.MovementHandler movementHandler) {
        this.movementHandler = movementHandler;
        Bukkit.imanity().getMovementService().registerMovementHandler(plugin, this);
    }

    @Override
    public void onUpdateLocation(Player player, Location from, Location to, MovementPacketWrapper movementPacketWrapper) {
        movementHandler.onUpdateLocation(player, from, to);
    }

    @Override
    public void onUpdateRotation(Player player, Location from, Location to, MovementPacketWrapper movementPacketWrapper) {
        movementHandler.onUpdateRotation(player, from, to);
    }
}
