package rip.diamond.practice.match.listener;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
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
import rip.diamond.practice.event.KitLoadoutReceivedEvent;
import rip.diamond.practice.event.MatchStartEvent;
import rip.diamond.practice.event.MatchStateChangeEvent;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchEntity;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MatchListener implements Listener {

    private final Eden plugin;

    @EventHandler
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();
        Kit kit = match.getKit();

        match.getMatchPlayers().forEach(p -> {
            if (plugin.getKitEditorManager().isEditing(p)) {
                plugin.getKitEditorManager().leaveKitEditor(p, false);
            }

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

            if ((match.getKit().getGameRules().isBed() && !match.getTeam(player).isBedDestroyed()) || match.getKit().getGameRules().isGoal()) {
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
                    Item item = Util.dropItemNaturally(player.getLocation(), itemStack, player);
                    match.addDroppedItem(item, null); //Already modified the f value of EntityItem, therefore no need to put anything in 2nd variables
                }
            }
        }

        player.setHealth(20);
        player.setVelocity(new Vector());
        Util.teleport(player, player.getLocation().clone().add(0,2,0)); //Teleport 2 blocks higher, to try to re-do what MineHQ did (Make sure to place this line of code after setHealth, otherwise it won't work)

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
            if (match.getKit().getGameRules().isNoDamage()) {
                event.setDamage(0);
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
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
            Player entity = (Player) event.getEntity();
            Player damager = event.getDamager() instanceof Arrow ? (Player) ((Arrow) event.getDamager()).getShooter() : (Player) event.getDamager();

            //Damager might be null because there might be a chance when arrow hit the entity, the damager isn't online
            if (damager == null) {
                event.setCancelled(true);
                return;
            }

            PlayerProfile entityProfile = PlayerProfile.get(entity);
            PlayerProfile damagerProfile = PlayerProfile.get(damager);

            if (entityProfile.getPlayerState() == PlayerState.IN_MATCH && damagerProfile.getPlayerState() == PlayerState.IN_MATCH && entityProfile.getMatch() == damagerProfile.getMatch()) {
                Match match = entityProfile.getMatch();

                //It is cancelled in EntityDamageEvent. Check this again to prevent Boxing hits.
                if (match.getState() != MatchState.FIGHTING) {
                    event.setCancelled(true);
                    return;
                }

                Team teamEntity = match.getTeam(entity);
                Team teamDamager = match.getTeam(damager);

                //檢查攻擊方和被攻擊方是不是同隊
                if (teamEntity == teamDamager && entity != damager) {
                    event.setCancelled(true);
                    return;
                }

                //檢查職業是否只允許遠程攻擊傷害
                if (match.getKit().getGameRules().isProjectileOnly() && event.getDamager() instanceof Player) {
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

                //顯示造成的傷害
                if (event.getDamager() instanceof Arrow) {
                    Util.sendArrowHitMessage(event);
                }

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
            match.addDroppedItem(item, player.getName());
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
                        Common.playSound(player, Sound.EAT);
                        player.removePotionEffect(PotionEffectType.REGENERATION);
                        player.removePotionEffect(PotionEffectType.ABSORPTION);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                        player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
                        player.setItemInHand(new ItemBuilder(player.getItemInHand()).amount(player.getItemInHand().getAmount() - 1).build());
                        player.updateInventory();
                    }
                } else if (itemStack.getType() == Material.ENDER_PEARL && match.getKit().getGameRules().isEnderPearlCooldown() && action.name().startsWith("RIGHT_")) {
                    if (profile.getCooldowns().containsKey("enderpearl")) {
                        String time = TimeUtil.millisToSeconds(profile.getCooldowns().get("enderpearl").getRemaining());
                        Language.MATCH_USE_AGAIN_ENDER_PEARL.sendMessage(player, time);
                        event.setCancelled(true);
                    } else {
                        profile.getCooldowns().put("enderpearl", new Cooldown(16) {
                            @Override
                            public void run() {
                                if (isExpired()) {
                                    Language.MATCH_CAN_USE_ENDERPEARL.sendMessage(player);
                                    if (player.getLevel() > 0) player.setLevel(0);
                                    if (player.getExp() > 0.0F) player.setExp(0.0F);
                                } else {
                                    int seconds = Math.round(profile.getCooldowns().get("enderpearl").getRemaining()) / 1000;

                                    player.setLevel(seconds);
                                    player.setExp(profile.getCooldowns().get("enderpearl").getRemaining() / 16000F);
                                }
                            }
                        });
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
            if (match.getState() == MatchState.STARTING && match.getKit().getGameRules().isStartFreeze()) {
                event.setCancelled(true);
                return;
            }
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
                TeamPlayer teamPlayer = match.getTeamPlayer(player);

                new MatchClearBlockTask(match, 10, block.getWorld(), block.getLocation(), (itemStacks) -> {
                    if (player.isOnline() && match == profile.getMatch() && !teamPlayer.isRespawning() && teamPlayer.isAlive()) {
                        itemStacks.forEach(i -> player.getInventory().addItem(i));
                    }
                });
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
                    match.addDroppedItem(item, player.getName());
                });
            }
            block.setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            PlayerProfile profile = PlayerProfile.get(player);
            //PracticePlayer may be null because player left the server but the potion still in there
            if (profile == null) {
                return;
            }
            if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                Match match = profile.getMatch();
                if (match.getState() != MatchState.FIGHTING) {
                    return;
                }
                if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                    return;
                }
                if (event.getIntensity(player) <= 0.5D) {
                    match.getTeamPlayer(player).addPotionsMissed();
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            PlayerProfile profile = PlayerProfile.get(player);

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
                match.getEntities().add(new MatchEntity(projectile));
                if (projectile instanceof ThrownPotion) {
                    match.getTeamPlayer(player).addPotionsThrown();
                } else if (projectile instanceof Arrow && match.getKit().getGameRules().isGiveBackArrow()) {
                    profile.getCooldowns().put("arrow", new Cooldown(3500L) {
                        @Override
                        public void run() {
                            if (isExpired()) {
                                if (!player.getInventory().contains(Material.ARROW)) {
                                    int slot = -1;
                                    //No KitLoadout is received. This will be null when a player didn't select a kit
                                    //Should not happen anymore because kitLoadout is now automatically applied, but just in-case
                                    if (match.getTeamPlayer(player).getKitLoadout() != null) {
                                        for (int i = 0; i < 36; i++) {
                                            if (match.getTeamPlayer(player).getKitLoadout().getContents()[i] != null && match.getTeamPlayer(player).getKitLoadout().getContents()[i].getType() == Material.ARROW) slot = i;
                                        }
                                    }
                                    if (slot == -1 || player.getInventory().getItem(slot) != null) {
                                        player.getInventory().addItem(new ItemStack(Material.ARROW));
                                    } else {
                                        player.getInventory().setItem(slot, new ItemStack(Material.ARROW));
                                    }
                                }
                                if (player.getLevel() > 0) player.setLevel(0);
                                if (player.getExp() > 0.0F) player.setExp(0.0F);
                            } else {
                                int seconds = Math.round(profile.getCooldowns().get("arrow").getRemaining()) / 1000;

                                player.setLevel(seconds);
                                player.setExp(profile.getCooldowns().get("arrow").getRemaining() / 3500F);
                            }
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (event.getEntityType() == EntityType.ARROW) {
            projectile.remove();
        }
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            PlayerProfile profile = PlayerProfile.get(player);
            if (profile == null) {
                return;
            }
            if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                Match match = profile.getMatch();
                if (match.getState() != MatchState.FIGHTING) {
                    return;
                }
                if (!match.getTeamPlayer(player).isAlive() || match.getTeamPlayer(player).isRespawning()) {
                    return;
                }
                match.getEntities().removeIf(matchEntity -> matchEntity.getEntity().getEntityId() == projectile.getEntityId());
            }
        }
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            if (!Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(event.getItem())) {
                return;
            }

            if (player.getLastDamageCause() != null && player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof FishHook) {
                    event.setCancelled(true);
                }
            }
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

        if (kit.getGameRules().isBed() || kit.getGameRules().isPoint(match)) {
            match.getTeam(player).dye(teamPlayer);
        }
        player.updateInventory();
    }

}
