package rip.diamond.practice.debug;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.CommandManager;

public class TestListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof EnderPearl) {
            Common.broadcastMessage(CC.BLUE + "ProjectileLaunchEvent: " + event.isCancelled());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType() == Material.ENDER_PEARL) {
            Common.broadcastMessage(CC.BLUE + "PlayerInteractEvent: " + event.useItemInHand().name());
        }
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent event) {
        World from = event.getFrom();
        Player player = event.getPlayer();
        Common.broadcastMessage(CC.BLUE +  player.getName() + " - " + from.getName() + " to " + player.getWorld().getName());
    }

}
