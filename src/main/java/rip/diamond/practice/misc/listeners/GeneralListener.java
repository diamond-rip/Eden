package rip.diamond.practice.misc.listeners;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldLoadEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.misc.task.EloResetTask;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.TaskTicker;

import java.util.List;

@RequiredArgsConstructor
public class GeneralListener implements Listener {

    private final Eden plugin;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (TaskTicker.getTickers().stream().anyMatch(taskTicker -> taskTicker instanceof EloResetTask)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.RED + "ELO is resetting...");
        }
    }

    @EventHandler
    public void onLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            entity.remove();
        }
    }

    /*@EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }*/

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Material type = event.getBlock().getType();
        if (!Config.MATCH_REMOVE_CACTUS_SUGAR_CANE_PHYSICS.toBoolean()) {
            return;
        }
        if(type == Material.SUGAR_CANE_BLOCK || type == Material.CACTUS) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    //防止踐踏農作物
    @EventHandler
    public void onCropsTrampling(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatePortal(PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEnterPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGrowth(BlockGrowEvent event) {
        event.setCancelled(true);
    }

}
