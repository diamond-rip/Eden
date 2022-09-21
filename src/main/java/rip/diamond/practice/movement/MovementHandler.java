package rip.diamond.practice.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.imanity.imanityspigot.packet.wrappers.MovementPacketWrapper;
import rip.diamond.practice.Eden;
import rip.diamond.practice.hook.spigot.SpigotType;

public abstract class MovementHandler {

    private final Eden plugin = Eden.INSTANCE;

    public MovementHandler() {
        if (plugin.getHookManager().getSpigotController().getSpigotType() == SpigotType.IMANITY_SPIGOT_3) {
            Bukkit.imanity().getMovementService().registerMovementHandler(plugin, new org.imanity.imanityspigot.movement.MovementHandler() {
                @Override
                public void onUpdateLocation(Player player, Location from, Location to, MovementPacketWrapper movementPacketWrapper) {
                    MovementHandler.this.onUpdateLocation(player, from, to);
                }

                @Override
                public void onUpdateRotation(Player player, Location from, Location to, MovementPacketWrapper movementPacketWrapper) {
                    MovementHandler.this.onUpdateRotation(player, from, to);
                }
            });
        } else {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onMove(PlayerMoveEvent event) {
                    Player player = event.getPlayer();

                    double fromX = event.getFrom().getX();
                    double fromY = event.getFrom().getY();
                    double fromZ = event.getFrom().getZ();
                    double fromYaw = event.getFrom().getYaw();
                    double fromPitch = event.getFrom().getPitch();

                    double toX = event.getTo().getX();
                    double toY = event.getTo().getY();
                    double toZ = event.getTo().getZ();
                    double toYaw = event.getTo().getYaw();
                    double toPitch = event.getTo().getPitch();

                    if (fromX != toX || fromY != toY || fromZ != toZ) {
                        plugin.getMovementHandler().onUpdateLocation(player, event.getFrom(), event.getTo());
                    }

                    if (fromYaw != toYaw || fromPitch != toPitch) {
                        plugin.getMovementHandler().onUpdateRotation(player, event.getFrom(), event.getTo());
                    }
                }
            }, plugin);
        }
    }

    public abstract void onUpdateLocation(Player player, Location from, Location to);

    public abstract void onUpdateRotation(Player player, Location from, Location to);

}
