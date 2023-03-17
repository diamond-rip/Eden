package rip.diamond.practice.profile.listener;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import rip.diamond.practice.Eden;
import rip.diamond.practice.EdenItems;
import rip.diamond.practice.Language;
import rip.diamond.practice.event.PlayerProfileLoadedEvent;
import rip.diamond.practice.event.SettingsChangeEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchEntity;
import rip.diamond.practice.match.menu.SpectateTeleportMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.option.Option;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final Eden plugin;

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        if (PlayerProfile.get(player) != null) {
            player.kickPlayer(CC.RED + "[Eden] Please wait for a few seconds before re-login");
            return;
        }
        PlayerProfile profile = PlayerProfile.createPlayerProfile(player);

        //Reset their inventory and their location, to prevent player stuck in other places or contains illegal items
        PlayerUtil.reset(player);
        plugin.getLobbyManager().teleport(player);

        profile.load((success) -> {
            if (!success) {
                Tasks.run(()-> player.kickPlayer(CC.RED + "[Eden] Unable to load your data. Please try to re-login in a few seconds"));
            } else {
                Language.JOIN_MESSAGE.sendListOfMessage(player);
                plugin.getLobbyManager().sendToSpawnAndReset(player);
                profile.getSettings().get(ProfileSettings.TIME_CHANGER).run(player);

                PlayerProfileLoadedEvent e = new PlayerProfileLoadedEvent(player, profile);
                e.call();
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile == null) { //當在 PlayerJoinEvent 未能加載資料的時候, profile 就會是 null
            Common.log(player.getName() + "'s profile is not saved due to the profile is null");
            return;
        }

        profile.save(true, (success) -> {
            if (success) {
                PlayerProfile.getProfiles().remove(player.getUniqueId());
            } else {
                Common.log(CC.RED + "[Eden] Unable to save " + player.getName() + "'s profile. Data is not going to clear.");
            }
        });
    }

    //This event is only to prevent players to open block's inventory. We still allow plugins to run all PlayerInteractEvent
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() != PlayerState.IN_MATCH) {
            event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
            //We don't stop the process here, continue the check
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (event.getAction().name().startsWith("RIGHT_")) {
            if (profile.getPlayerState() == PlayerState.LOADING) {
                Common.sendMessage(player, CC.RED + "[Eden] System is loading your profile... Please wait for a few seconds.");
                return;
            }

            //Lobby items
            if (item.equals(EdenItems.LOBBY_UNRANKED_QUEUE.getItemStack())) {
                Util.performCommand(player, "eden:queue unranked");
            } else if (item.equals(EdenItems.LOBBY_RANKED_QUEUE.getItemStack())) {
                Util.performCommand(player, "eden:queue ranked");
            } else if (item.equals(EdenItems.LOBBY_CREATE_EVENT.getItemStack())) {
                Util.performCommand(player, "eden:event create");
            } else if (item.equals(EdenItems.LOBBY_JOIN_EVENT.getItemStack())) {
                Util.performCommand(player, "eden:joinevent");
            } else if (item.equals(EdenItems.LOBBY_PARTY_OPEN.getItemStack())) {
                Util.performCommand(player, "eden:party create");
            } else if (item.equals(EdenItems.LOBBY_LEADERBOARD.getItemStack())) {
                Util.performCommand(player, "eden:stats");
            } else if (item.equals(EdenItems.LOBBY_SETTINGS.getItemStack())) {
                Util.performCommand(player, "eden:settings");
            } else if (item.equals(EdenItems.LOBBY_EDITOR.getItemStack())) {
                Util.performCommand(player, "eden:editkits");
            }
            //Party items
            else if (item.equals(EdenItems.PARTY_PARTY_LIST.getItemStack())) {
                Util.performCommand(player, "eden:party list");
            } else if (item.equals(EdenItems.PARTY_PARTY_FIGHT.getItemStack())) {
                Util.performCommand(player, "eden:choosematchtype");
            } else if (item.equals(EdenItems.PARTY_OTHER_PARTIES.getItemStack())) {
                Util.performCommand(player, "eden:otherparties");
            } else if (item.equals(EdenItems.PARTY_EDITOR.getItemStack())) {
                Util.performCommand(player, "eden:editkits");
            } else if (item.equals(EdenItems.PARTY_PARTY_LEAVE.getItemStack())) {
                Util.performCommand(player, "eden:party leave");
            }
            //Queue items
            else if (item.equals(EdenItems.QUEUE_LEAVE_QUEUE.getItemStack())) {
                Util.performCommand(player, "eden:queue leave");
            }
            //Match items
            else if (item.equals(EdenItems.MATCH_REQUEUE.getItemStack())) {
                Match match = profile.getMatch();
                if (match == null) {
                    Language.MATCH_REQUEUE_NOT_IN_MATCH.sendMessage(player);
                    return;
                }
                plugin.getLobbyManager().sendToSpawnAndReset(player);
                Tasks.runLater(() -> Queue.joinQueue(player, match.getKit(), match.getQueueType()), 1L);
            }
            //Spectate items
            else if (item.equals(EdenItems.SPECTATE_TELEPORTER.getItemStack())) {
                new SpectateTeleportMenu(profile.getMatch()).openMenu(player);
            } else if (item.equals(EdenItems.SPECTATE_LEAVE_SPECTATE.getItemStack())) {
                Util.performCommand(player, "eden:leavespectate");
            } else if (item.equals(EdenItems.SPECTATE_TOGGLE_VISIBILITY_OFF.getItemStack()) || item.equals(EdenItems.SPECTATE_TOGGLE_VISIBILITY_ON.getItemStack())) {
                ProfileSettings settings = ProfileSettings.SPECTATOR_VISIBILITY;
                Option currentOption = profile.getSettings().get(settings);

                profile.getSettings().replace(settings, settings.getNextOption(currentOption));
                SettingsChangeEvent e = new SettingsChangeEvent(player, profile, settings);
                e.call();
            }
        }
    }


    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!Checker.canDamage(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!Checker.canDamage(player)) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_MATCH) {
            event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (!Checker.canDamage(event.getPlayer())) {
            event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!Checker.canDamage(player)) {
            event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!Checker.canDamage(player)) {
            event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        //防止玩家移動物品欄中的物品
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            PlayerProfile profile = PlayerProfile.get(player);

            if (event.getClickedInventory() instanceof PlayerInventory && !profile.getPlayerState().isAbleToMoveItemInInventory()) {
                event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.LOADING) {
            Common.sendMessage(player, CC.RED + "[Eden] System is loading your profile... Please wait for a few seconds.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSettingsChange(SettingsChangeEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = event.getProfile();
        ProfileSettings settings = event.getSettings();

        if (profile.getMatch() != null && settings == ProfileSettings.SPECTATOR_VISIBILITY) {
            VisibilityController.updateVisibility(player);
            profile.setupItems();
        }
    }

}
