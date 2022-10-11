package rip.diamond.practice.movement.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.movement.MovementHandler;

public class DefaultMovementHandler {

    public DefaultMovementHandler(Eden plugin, MovementHandler movementHandler) {

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
                    movementHandler.onUpdateLocation(player, event.getFrom(), event.getTo());
                }

                if (fromYaw != toYaw || fromPitch != toPitch) {
                    movementHandler.onUpdateRotation(player, event.getFrom(), event.getTo());
                }
            }
        }, plugin);
    }

}
