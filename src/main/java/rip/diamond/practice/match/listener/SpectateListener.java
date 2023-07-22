package rip.diamond.practice.match.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.Util;

public class SpectateListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile == null) {
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING && profile.getMatch() != null) {
            Match match = profile.getMatch();

            match.leaveSpectate(player);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerProfile profile = PlayerProfile.get(player);

            if (Util.isNPC(player)) {
                return;
            }

            if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerProfile attackProfile = PlayerProfile.get(attacker);

            if (Util.isNPC(attacker)) {
                return;
            }

            if (attackProfile.getPlayerState() == PlayerState.IN_SPECTATING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerProfile profile = PlayerProfile.get(player);

            if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            PlayerProfile profile = PlayerProfile.get(player);
            if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Try to fix player fly state when player changes world
     * A fix for <a href="https://github.com/diamond-rip/Eden/issues/374">#374</a>
     */
    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
            Tasks.runLater(() -> {
                player.setAllowFlight(true);
                player.setFlying(true);

                player.setAllowFlight(true);
                player.setFlying(true);
            }, 2L);
        }
    }

    /**
     * Don't let spectators be affected by potions dropped near them
     */
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                PlayerProfile profile = PlayerProfile.get(player);
                if (profile.getPlayerState() == PlayerState.IN_SPECTATING) {
                    event.setIntensity(entity, 0F);
                }
            }
        }
    }

}
