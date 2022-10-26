package rip.diamond.practice.movement.type;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import xyz.refinedev.spigot.api.handlers.PacketAPI;
import xyz.refinedev.spigot.api.handlers.impl.MovementHandler;

public class CarbonSpigotMovementHandler implements MovementHandler {

    private final rip.diamond.practice.movement.MovementHandler movementHandler;

    public CarbonSpigotMovementHandler(Eden plugin, rip.diamond.practice.movement.MovementHandler movementHandler) {
        this.movementHandler = movementHandler;
        PacketAPI.getInstance().registerMovementHandler(plugin, this);
    }

    @Override
    public void handleUpdateLocation(Player player, Location from, Location to, PacketPlayInFlying packetPlayInFlying) {
        movementHandler.onUpdateLocation(player, from, to);
    }

    @Override
    public void handleUpdateRotation(Player player, Location from, Location to, PacketPlayInFlying packetPlayInFlying) {
        movementHandler.onUpdateRotation(player, from, to);
    }
}
