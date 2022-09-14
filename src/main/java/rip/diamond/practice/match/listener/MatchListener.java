package rip.diamond.practice.match.listener;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.event.KitLoadoutReceivedEvent;
import rip.diamond.practice.event.MatchStartEvent;
import rip.diamond.practice.event.MatchStateChangeEvent;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.MatchType;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.task.MatchClearBlockTask;
import rip.diamond.practice.match.task.MatchRespawnTask;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.cuboid.Cuboid;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MatchListener implements Listener {

    private final Eden plugin;

    @EventHandler
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();

        match.getMatchPlayers().forEach(p -> {
            String opponents;
            switch (match.getMatchType()) {
                case SOLO:
                    opponents = match.getOpponent(match.getTeamPlayer(p)).getUsername();
                    break;
                case SPLIT:
                    opponents = ((TeamMatch)match).getOpponentTeam(p).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(Language.MATCH_SEPARATE.toString()));
                    break;
                case FFA:
                    opponents = match.getTeams().stream().map(team -> team.getLeader().getUsername()).collect(Collectors.joining(Language.MATCH_SEPARATE.toString()));
                    break;
                default:
                    opponents = CC.RED + "ERROR";
                    break;
            }

            if (match.getQueueType() == QueueType.UNRANKED) {
                Language.MATCH_START_UNRANKED.toStringList(match.getMatchType().getReadable(), kit.getDisplayName(), match.getArenaDetail().getArena().getName(), opponents).forEach(s -> {
                    Common.sendMessage(p, s);
                });
            } else if (match.getQueueType() == QueueType.RANKED && match.getMatchType() == MatchType.SOLO) {
                int elo = PlayerProfile.get(match.getOpponent(match.getTeamPlayer(p)).getUuid()).getKitData().get(kit.getName()).getElo();
                Language.MATCH_START_RANKED.toStringList(match.getMatchType().getReadable(), kit.getDisplayName(), match.getArenaDetail().getArena().getName(), opponents, elo).forEach(s -> {
                    Common.sendMessage(p, s);
                });
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        //Profile will be null if the profile is not loaded in AsyncPlayerPreLoginEvent
        if (profile == null) {
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            match.die(player, true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player = event.getEntity();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            KitGameRules gameRules = match.getKit().getGameRules();

            if ((match.getKit().getGameRules().isBed() && !match.getTeam(player).isBedDestroyed()) || match.getKit().getGameRules().isBridge()) {
                new MatchRespawnTask(match, match.getTeamPlayer(player));
            } else {
                match.die(player, false);
            }

            if (gameRules.isDropItemWhenDie()) {
                //This drops List will filter useless and banned items, and pots/bowls if the match is ending
                List<ItemStack> drops = event.getDrops();
                drops.removeIf(i -> i == null || i.getType() == Material.AIR || i.getType() == Material.BOOK || i.getType() == Material.ENCHANTED_BOOK);
                if (match.canEnd()) {
                    drops.removeIf(i -> i.getType() == Material.POTION || i.getType() == Material.GLASS_BOTTLE || i.getType() == Material.MUSHROOM_SOUP || i.getType() == Material.BOWL);
                }
                for (ItemStack itemStack : drops) {
                    Item item = player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    match.addDroppedItem(item);
                }
            }
        }

        player.setHealth(20);
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().clone().add(0, 2, 0)); //Teleport 2 blocks higher, to try to re-do what MineHQ did (Make sure to place this line of code after setHealth, otherwise it won't work)

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(player);

            if (teamPlayer.getProtectionUntil() > System.currentTimeMillis()) {
                event.setCancelled(true);
                return;
            }
            if (teamPlayer.isRespawning()) {
                event.setCancelled(true);
                return;
            }
            if (!teamPlayer.isAlive()) {
                event.setCancelled(true);
                return;
            }
            if (match.getKit().getGameRules().isNoFallDamage() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                Util.damage(player, 99999);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player entity = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        PlayerProfile entityProfile = PlayerProfile.get(entity);
        PlayerProfile damagerProfile = PlayerProfile.get(damager);

        if (entityProfile.getPlayerState() == PlayerState.IN_MATCH && damagerProfile.getPlayerState() == PlayerState.IN_MATCH && entityProfile.getMatch() == damagerProfile.getMatch()) {
            Match match = entityProfile.getMatch();

            Team teamEntity = match.getTeam(entity);
            Team teamDamager = match.getTeam(damager);

            //檢查攻擊方和被攻擊方是不是同隊
            if (teamEntity == teamDamager && entity != damager) {
                event.setCancelled(true);
                return;
            }

            //檢查職業是否只允許遠程攻擊傷害 (這裏的 damager 一定會是玩家, 所以不需要檢查 damager 是不是遠程攻擊)
            if (match.getKit().getGameRules().isProjectileOnly()) {
                event.setCancelled(true);
                return;
            }

            if (match.getKit().getGameRules().isBoxing() || match.getKit().getGameRules().isNoDamage()) {
                event.setDamage(0);
                entity.setHealth(20.0);
            }

            TeamPlayer teamPlayerEntity = match.getTeamPlayer(entity);
            TeamPlayer teamPlayerDamager = match.getTeamPlayer(damager);

            if (!teamPlayerEntity.isAlive() || !teamPlayerDamager.isAlive() || teamPlayerEntity.isRespawning() || teamPlayerDamager.isRespawning()) {
                event.setCancelled(true);
                return;
            }

            teamPlayerDamager.handleHit(event.getFinalDamage());
            teamPlayerEntity.handleGotHit(match.getTeamPlayer(damager));

            //檢查職業是否為Boxing, 和檢查是否達到最大Boxing攻擊數, 如果是的話就死亡
            if (match.getKit().getGameRules().isBoxing() && match.getTeam(entity).getGotHits() >= match.getMaximumBoxingHits()) {
                switch (match.getMatchType()) {
                    case SOLO:
                        Util.damage(entity, 99999);
                        break;
                    case SPLIT:
                    case FFA:
                        match.getTeam(entity).getAliveTeamPlayers().forEach(teamPlayer -> Util.damage(entity, 99999));
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Item item = event.getItemDrop();

        if (Checker.canDamage(player)) {
            if (item.getItemStack().getType().name().contains("_SWORD") && player.getInventory().getHeldItemSlot() == 0) {
                Language.MATCH_CANNOT_DROP_WEAPON.sendMessage(player);
                event.setCancelled(true);
                return;
            }

            if (item.getItemStack().getType() == Material.GLASS_BOTTLE) {
                item.remove();
                return;
            }

            Match match = profile.getMatch();
            match.addDroppedItem(item);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Item item = event.getItem();

        if (Checker.canDamage(player)) {
            if (item.getItemStack().getType().name().contains("BOOK")) {
                event.setCancelled(true);
                return;
            }

            Match match = profile.getMatch();
            boolean found = match.getEntities().stream().anyMatch(matchEntity -> matchEntity.getEntity().getEntityId() == item.getEntityId());
            if (found) {
                match.getEntities().removeIf(matchEntity -> matchEntity.getEntity() == item);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        Location from = event.getFrom();
        Location to = event.getTo();

        Block block = event.getTo().getBlock();
        Block underBlock = event.getTo().clone().add(0, -1, 0).getBlock();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            Arena arena = match.getArenaDetail().getArena();
            Kit kit = match.getKit();
            KitGameRules gameRules = kit.getGameRules();

            if (gameRules.isStartFreeze() && match.getState() == MatchState.STARTING && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                player.teleport(from);
                return;
            }

            if (arena.getYLimit() > player.getLocation().getY()) {
                Util.damage(player, 99999);
                return;
            }

            //Prevent any duplicate scoring
            //If two people go into the portal at the same time in bridge, it will count as +2 points
            //If player go into the water and PlayerMoveEvent is too slow to perform teleportation, it will run MatchNewRoundTask multiple times
            if (match.getMatchPlayers().stream().filter(Objects::nonNull).noneMatch(p -> PlayerProfile.get(p).getCooldowns().containsKey("score"))) {
                //檢查 KitGameRules 水上即死
                if (gameRules.isDeathOnWater() && match.getState() == MatchState.FIGHTING && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)) {
                    if (gameRules.isPoint()) {
                        match.score(profile, match.getTeamPlayer(player).getLastHitDamager());
                    } else {
                        Util.damage(player, 99999);
                    }
                    return;
                }

                //檢查 KitGameRules 是否為戰橋模式
                if (gameRules.isBridge() && match.getState() == MatchState.FIGHTING && underBlock.getType() == Material.ENDER_PORTAL) {
                    Team team = match.getTeam(player);
                    //Prevent player scoring their own goal
                    if (team.getSpawnLocation().distance(to) > 30) {
                        match.score(profile, match.getTeamPlayer(player));
                    } else {
                        Util.damage(player, 99999);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();

            if (event.getItem().getType() == Material.GOLDEN_APPLE) {
                if (match.getKit().getGameRules().isHypixelUHC()) {
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                    player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
                    return;
                } else if (match.getKit().getGameRules().isInstantGapple()) {
                    event.setCancelled(true);
                    player.setHealth(20);
                    ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
                    player.setItemInHand(new ItemBuilder(player.getItemInHand()).amount(player.getItemInHand().getAmount() - 1).build());
                    return;
                } else if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                    player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Action action = event.getAction();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();

            ItemStack itemStack = event.getItem();
            if (itemStack != null) {
                if (itemStack.getType() == Material.SKULL_ITEM && match.getKit().getGameRules().isHypixelUHC()) {
                    if (profile.getCooldowns().containsKey("goldenhead")) {
                        String time = TimeUtil.millisToSeconds(profile.getCooldowns().get("goldenhead").getRemaining());
                        Language.MATCH_USE_AGAIN_GOLDEN_HEAD.sendMessage(player, time);
                        event.setCancelled(true);
                    } else {
                        profile.getCooldowns().put("goldenhead", new Cooldown(1));
                    }
                } else if (itemStack.getType() == Material.ENDER_PEARL && match.getKit().getGameRules().isEnderPearlCooldown() && action.name().startsWith("RIGHT_")) {
                    if (profile.getCooldowns().containsKey("enderpearl")) {
                        String time = TimeUtil.millisToSeconds(profile.getCooldowns().get("enderpearl").getRemaining());
                        Language.MATCH_USE_AGAIN_ENDER_PEARL.sendMessage(player, time);
                        event.setCancelled(true);
                    } else {
                        profile.getCooldowns().put("enderpearl", new Cooldown(16));
                    }
                } else if (itemStack.getType() == Material.BOOK || itemStack.getType() == Material.ENCHANTED_BOOK) {
                    net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                    if (nmsItem.hasTag()) {
                        NBTTagCompound compound = nmsItem.getTag();
                        if (compound.hasKey("name") && compound.hasKey("armor") && compound.hasKey("contents")) {
                            String name = compound.getString("name");
                            String armor = compound.getString("armor");
                            String contents = compound.getString("contents");
                            KitLoadout kitLoadout = new KitLoadout(name, armor, contents);

                            kitLoadout.apply(match, player);
                            Language.MATCH_RECEIVED_KIT_LOADOUT.sendMessage(player, name);
                        }
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onRegain(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                Player player = (Player) event.getEntity();
                PlayerProfile profile = PlayerProfile.get(player);

                if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                    Match match = profile.getMatch();
                    if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                        return;
                    }
                    if (!match.getKit().getGameRules().isHealthRegeneration()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerProfile profile = PlayerProfile.get(player);

            if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                Match match = profile.getMatch();
                if (match.getState() != MatchState.FIGHTING) {
                    event.setCancelled(true);
                    return;
                }
                if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                    event.setCancelled(true);
                    return;
                }
                if (!match.getKit().getGameRules().isFoodLevelChange()) {
                    event.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        int x = (int) block.getLocation().getX();
        int y = (int) block.getLocation().getY();
        int z = (int) block.getLocation().getZ();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (match.getState() == MatchState.ENDING) {
                event.setCancelled(true);
                return;
            }
            if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                event.setCancelled(true);
                return;
            }
            if (!match.getKit().getGameRules().isBuild()) {
                event.setCancelled(true);
                return;
            }
            if (match.isProtected(block.getLocation(), true)) {
                event.setCancelled(true);
                return;
            }

            Cuboid cuboid = match.getArenaDetail().getCuboid();
            if (x < cuboid.getX1() || x > cuboid.getX2() || y < cuboid.getY1() || y > cuboid.getY2() || z < cuboid.getZ1() || z > cuboid.getZ2()) {
                Language.MATCH_CANNOT_BUILD_OUTSIDE.sendMessage(player);
                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().add(block.getLocation());
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        int x = (int) block.getLocation().getX();
        int y = (int) block.getLocation().getY();
        int z = (int) block.getLocation().getZ();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (match.getState() == MatchState.ENDING) {
                event.setCancelled(true);
                return;
            }
            if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                event.setCancelled(true);
                return;
            }
            if (!match.getKit().getGameRules().isBuild()) {
                event.setCancelled(true);
                return;
            }
            if (match.isProtected(block.getLocation(), true)) {
                event.setCancelled(true);
                return;
            }

            Cuboid cuboid = match.getArenaDetail().getCuboid();
            if (x < cuboid.getX1() || x > cuboid.getX2() || y < cuboid.getY1() || y > cuboid.getY2() || z < cuboid.getZ1() || z > cuboid.getZ2()) {
                Language.MATCH_CANNOT_BUILD_OUTSIDE.sendMessage(player);
                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().remove(block.getLocation());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlockPlaced();
        int x = (int) block.getLocation().getX();
        int y = (int) block.getLocation().getY();
        int z = (int) block.getLocation().getZ();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (match.getState() == MatchState.ENDING) {
                event.setCancelled(true);
                return;
            }
            if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                event.setCancelled(true);
                return;
            }
            if (!match.getKit().getGameRules().isBuild()) {
                event.setCancelled(true);
                return;
            }
            if (match.isProtected(block.getLocation(), true)) {
                event.setCancelled(true);
                return;
            }

            Cuboid cuboid = match.getArenaDetail().getCuboid();
            if (x < cuboid.getX1() || x > cuboid.getX2() || y < cuboid.getY1() || y > cuboid.getY2() || z < cuboid.getZ1() || z > cuboid.getZ2()) {
                Language.MATCH_CANNOT_BUILD_OUTSIDE.sendMessage(player);
                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().add(block.getLocation());

            if (match.getKit().getGameRules().isClearBlock()) {
                new MatchClearBlockTask(match, 10, block.getWorld(), Collections.singletonList(block.getLocation()));
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlock();

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (match.getState() != MatchState.FIGHTING) {
                event.setCancelled(true);
                return;
            }
            if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                event.setCancelled(true);
                return;
            }
            if (!match.getKit().getGameRules().isBuild()) {
                event.setCancelled(true);
                return;
            }
            if (match.isProtected(block.getLocation(), false)) {
                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().remove(block.getLocation());

            Kit kit = match.getKit();
            if (kit.getGameRules().isBed() && block.getType() == Material.BED_BLOCK) {
                //Now get the bed location
                Location bedLocation1 = new Location(block.getLocation().getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                Location bedLocation2 = Util.getBedBlockNearBy(bedLocation1).clone(); //因為一張床等於兩個方塊, 所以需要床的另一邊位置

                Team team = match.getTeam(player);
                Team opponentTeam = match.getTeams().stream().min(Comparator.comparing(t -> t.getSpawnLocation().distance(bedLocation1))).orElse(null);
                if (opponentTeam == null) {
                    throw new PracticeUnexpectedException("Cannot find the opponent team when player is destroying a bed (Match UUID: " + match.getUuid() + ")");
                }
                if (team == opponentTeam) {
                    Language.MATCH_CANNOT_BREAK_OWN_BED.sendMessage(player);
                    event.setCancelled(true);
                    return;
                }
                match.broadcastSound(team, Sound.ENDERDRAGON_GROWL);
                match.broadcastSound(opponentTeam, Sound.WITHER_DEATH);
                match.broadcastSpectatorsSound(Sound.ENDERDRAGON_GROWL);
                match.broadcastTitle(opponentTeam, Language.MATCH_BED_BREAK_TITLE.toString());
                match.broadcastSubTitle(opponentTeam, Language.MATCH_BED_BREAK_SUBTITLE.toString());
                match.broadcastMessage(Language.MATCH_BED_BREAK_MESSAGE.toStringList(opponentTeam.getTeamColor().getTeamName(), team.getTeamColor().getColor(), player.getName()));
                opponentTeam.setBedDestroyed(true);
                event.setCancelled(true);

                bedLocation1.getBlock().setType(Material.AIR);
                bedLocation2.getBlock().setType(Material.AIR);
                return;
            }
            if (kit.getGameRules().isSpleef()) {
                if (block.getType() == Material.SNOW_BLOCK && player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                }
            } else {
                block.getDrops().forEach(itemStack -> {
                    Item item = block.getLocation().getWorld().dropItemNaturally(block.getLocation().clone().add(0.5,0.5,0.5), itemStack);
                    match.addDroppedItem(item);
                });
            }
            block.setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMatchStateChange(MatchStateChangeEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();

        if (match.getState() == MatchState.FIGHTING) {
            for (TeamPlayer teamPlayer : match.getTeamPlayers()) {
                Player player = teamPlayer.getPlayer();
                if (player != null && teamPlayer.getKitLoadout() == null) {
                    kit.getKitLoadout().apply(match, player);
                    Language.MATCH_RECEIVED_KIT_LOADOUT_BECAUSE_TIMEOUT.sendMessage(player);
                }
            }
        }
    }

    @EventHandler
    public void onKitLoadoutReceived(KitLoadoutReceivedEvent event) {
        Player player = event.getPlayer();
        Match match = event.getMatch();
        TeamPlayer teamPlayer = match.getTeamPlayer(player);
        Kit kit = match.getKit();

        if (kit.getGameRules().isBed() || kit.getGameRules().isPoint()) {
            match.getTeam(player).dye(teamPlayer);
        }
        player.updateInventory();
    }

}
