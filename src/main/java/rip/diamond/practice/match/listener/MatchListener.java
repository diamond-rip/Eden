package rip.diamond.practice.match.listener;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.EdenSound;
import rip.diamond.practice.config.Language;
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
import rip.diamond.practice.profile.cooldown.Cooldown;
import rip.diamond.practice.profile.cooldown.CooldownType;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.cuboid.Cuboid;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;
import rip.diamond.practice.util.serialization.LocationSerialization;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
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

            PlayerProfile.get(p).getCooldowns().forEach((s, cooldown) -> cooldown.cancelCountdown());

            String opponents;
            switch (match.getMatchType()) {
                case SOLO:
                    opponents = match.getOpponent(match.getTeamPlayer(p)).getUsername();
                    break;
                case SPLIT:
                    opponents = ((TeamMatch)match).getOpponentTeam(p).getTeamPlayers().stream().map(TeamPlayer::getUsername).collect(Collectors.joining(Language.MATCH_SEPARATE.toString()));
                    break;
                case FFA:
                case SUMO_EVENT:
                    opponents = match.getTeams().stream().map(team -> team.getLeader().getUsername()).collect(Collectors.joining(Language.MATCH_SEPARATE.toString()));
                    break;
                default:
                    opponents = CC.RED + "ERROR";
                    break;
            }

            if (match.getQueueType() == QueueType.UNRANKED) {
                Language.MATCH_START_UNRANKED.toStringList(match.getMatchType().getReadable(), kit.getDisplayName(), match.getArenaDetail().getArena().getDisplayName(), opponents).forEach(s -> {
                    Common.sendMessage(p, s);
                });
            } else if (match.getQueueType() == QueueType.RANKED && match.getMatchType() == MatchType.SOLO) {
                int elo = PlayerProfile.get(match.getOpponent(match.getTeamPlayer(p)).getUuid()).getKitData().get(kit.getName()).getElo();
                Language.MATCH_START_RANKED.toStringList(match.getMatchType().getReadable(), kit.getDisplayName(), match.getArenaDetail().getArena().getDisplayName(), opponents, elo).forEach(s -> {
                    Common.sendMessage(p, s);
                });
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        //Profile will be null if the profile is not loaded in PlayerJoinEvent
        if (profile == null) {
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            match.die(player, true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (current != null && (current.getType() == Material.BOOK || current.getType() == Material.ENCHANTED_BOOK)) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(current);
            if (nmsItem.hasTag()) {
                NBTTagCompound compound = nmsItem.getTag();
                if (compound.hasKey("name") && compound.hasKey("armor") && compound.hasKey("contents")) {
                    event.setCancelled(true);
                }
            }
        } else if (cursor != null && (cursor.getType() == Material.BOOK || cursor.getType() == Material.ENCHANTED_BOOK)) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(cursor);
            if (nmsItem.hasTag()) {
                NBTTagCompound compound = nmsItem.getTag();
                if (compound.hasKey("name") && compound.hasKey("armor") && compound.hasKey("contents")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        Player player = event.getEntity();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            KitGameRules gameRules = match.getKit().getGameRules();

            if ((gameRules.isBed() && !match.getTeam(player).isBedDestroyed()) || gameRules.isBreakGoal() || gameRules.isPortalGoal()) {
                new MatchRespawnTask(match, teamPlayer);
            } else if (gameRules.isPoint(match)) {
                TeamPlayer lastHitDamager = teamPlayer.getLastHitDamager();
                //玩家有機會在不被敵方攻擊的情況下死亡, 例如岩漿, 如果是這樣, 就在敵方隊伍隨便抽一個玩家出來
                if (lastHitDamager == null) {
                    lastHitDamager = match.getOpponentTeam(match.getTeam(player)).getAliveTeamPlayers().get(0);
                }
                match.score(profile, teamPlayer, lastHitDamager);
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

            if (gameRules.isClearBlock()) {
                match.getTasks().stream()
                        .filter(task -> task instanceof MatchClearBlockTask)
                        .map(task -> (MatchClearBlockTask) task)
                        .filter(task -> task.getBlockPlacer() == teamPlayer)
                        .forEach(task -> task.setActivateCallback(false));
            }
        }

        player.setHealth(20);
        player.setVelocity(new Vector());

        event.setDroppedExp(0);
        event.getDrops().clear();

        if (Config.MATCH_TP_2_BLOCKS_UP_WHEN_DIE.toBoolean()) {
            Util.teleport(player, player.getLocation().clone().add(0,2,0)); //Teleport 2 blocks higher, to try to re-do what MineHQ did (Make sure to place this line of code after setHealth, otherwise it won't work)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) //A fix for #307 point 1 - try to cancel the hits which anticheat cancelled
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerProfile profile = PlayerProfile.get(player);

        //profile will be null when damaged player is a citizens player NPC, but is not a pvp bot
        if (profile == null) {
            return;
        }

        if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
            Match match = profile.getMatch();
            TeamPlayer teamPlayer = match.getTeamPlayer(player);
            KitGameRules rules = match.getKit().getGameRules();

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
            if (rules.isNoFallDamage() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (rules.isNoDamage() && !rules.isBoxing()) { //We handle boxing damages in MatchListener.onDamageEntity
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

    @EventHandler(priority = EventPriority.HIGHEST) //Allow the above EntityDamageEvent run first
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player || event.getDamager() instanceof FishHook || event.getDamager() instanceof Snowball || event.getDamager() instanceof Egg || event.getDamager() instanceof Arrow)) {
            Player entity = (Player) event.getEntity();
            Player damager = event.getDamager() instanceof Projectile ? (Player) ((Projectile) event.getDamager()).getShooter() : (Player) event.getDamager();

            //Damager might be null because there might be a chance when arrow hit the entity, the damager isn't online
            if (damager == null) {
                event.setCancelled(true);
                return;
            }

            PlayerProfile entityProfile = PlayerProfile.get(entity);
            PlayerProfile damagerProfile = PlayerProfile.get(damager);

            //profile will be null when damaged player is a citizens player NPC, but is not a pvp bot
            if (entityProfile == null) {
                return;
            }

            if (entityProfile.getPlayerState() == PlayerState.IN_MATCH && damagerProfile.getPlayerState() == PlayerState.IN_MATCH) {
                Match match = entityProfile.getMatch();
                Kit kit = match.getKit();

                if (damagerProfile.getMatch() != entityProfile.getMatch()) {
                    throw new PracticeUnexpectedException("Damager's match does not match with entity's match");
                }

                //It is cancelled in EntityDamageEvent. Check this again to prevent Boxing hits.
                if (match.getState() != MatchState.FIGHTING) {
                    event.setCancelled(true);
                    return;
                }

                Team teamEntity = match.getTeam(entity);
                Team teamDamager = match.getTeam(damager);

                //檢查攻擊方和被攻擊方是不是同隊
                if (teamEntity == teamDamager) {
                    //檢查攻擊方和被攻擊方是不是同一個人
                    if (entity != damager) {
                        if (!kit.getGameRules().isTeamProjectile() && event.getDamager() instanceof Projectile) {
                            event.setCancelled(true);
                            return;
                        } else if (event.getDamager() instanceof Player) {
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        if (!kit.getGameRules().isBowBoosting() && event.getDamager() instanceof Arrow) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                //檢查職業是否只允許遠程攻擊傷害
                if (kit.getGameRules().isProjectileOnly() && event.getDamager() instanceof Player) {
                    event.setCancelled(true);
                    return;
                }

                TeamPlayer teamPlayerEntity = match.getTeamPlayer(entity);
                TeamPlayer teamPlayerDamager = match.getTeamPlayer(damager);
                double damage = event.getDamage();

                if (!teamPlayerEntity.isAlive() || !teamPlayerDamager.isAlive() || teamPlayerEntity.isRespawning() || teamPlayerDamager.isRespawning()) {
                    event.setCancelled(true);
                    return;
                }

                if (kit.getGameRules().isBoxing() || kit.getGameRules().isNoDamage()) {
                    event.setDamage(0);
                    entity.setHealth(20.0);
                }

                if (kit.getGameRules().isBoxing()) {
                    //Check if the damage is critical damage
                    //The way bukkit handles critical damage is strange, because sometimes it might fire the same event two times with different damage
                    double predictDamage = 1 + DamageCalculator.getEnchantedDamage(damager.getItemInHand());
                    if (predictDamage > damage) {
                        return;
                    }
                }

                teamPlayerDamager.setProtectionUntil(0); //Fix for #307 point 2

                teamPlayerDamager.handleHit(event.getFinalDamage());
                teamPlayerEntity.handleGotHit(match.getTeamPlayer(damager), entity.isBlocking());

                //顯示造成的傷害
                if (event.getDamager() instanceof Arrow && entity != damager) {
                    Util.sendArrowHitMessage(event);
                }

                //檢查職業是否為Boxing, 和檢查是否達到最大Boxing攻擊數, 如果是的話就死亡
                if (kit.getGameRules().isBoxing() && match.getTeam(entity).getGotHits() >= match.getMaximumBoxingHits()) {
                    match.getTeam(entity).getAliveTeamPlayers().forEach(teamPlayer -> {
                        teamPlayer.setProtectionUntil(0); //Allow our system to damage the player
                        Util.damage(teamPlayer.getPlayer(), 99999);
                    });
                }
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Fireball && Config.MATCH_FIREBALL_ENABLED.toBoolean()) {
            Player player = (Player) event.getEntity();

            if (Config.MATCH_FIREBALL_KNOCKBACK_ENABLED.toBoolean()) {
                event.setCancelled(true);
                player.damage(event.getDamage() / Config.MATCH_FIREBALL_DIVIDE_DAMAGE.toDouble());
                Util.pushAway(player, event.getDamager().getLocation(), Config.MATCH_FIREBALL_KNOCKBACK_VERTICAL.toDouble(), Config.MATCH_FIREBALL_KNOCKBACK_HORIZONTAL.toDouble());
            } else {
                event.setDamage(event.getDamage() / Config.MATCH_FIREBALL_DIVIDE_DAMAGE.toDouble());
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof TNTPrimed && Config.MATCH_TNT_ENABLED.toBoolean()) {
            Player player = (Player) event.getEntity();

            if (Config.MATCH_TNT_ENABLED.toBoolean()) {
                event.setCancelled(true);
                player.damage(event.getDamage() / Config.MATCH_TNT_DIVIDE_DAMAGE.toDouble());
                Util.pushAway(player, event.getDamager().getLocation(), Config.MATCH_TNT_KNOCKBACK_VERTICAL.toDouble(), Config.MATCH_TNT_KNOCKBACK_HORIZONTAL.toDouble());
            } else {
                event.setDamage(event.getDamage() / Config.MATCH_TNT_DIVIDE_DAMAGE.toDouble());
            }
        }
    }

    @EventHandler
    public void onSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (item == null) {
            return;
        }

        if (item.getItemStack().getType() == Material.BED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Item item = event.getItemDrop();

        if (Checker.canDamage(player)) {
            Match match = profile.getMatch();

            if (!match.getKit().getGameRules().isDropItems()) {
                event.setCancelled(true);
                return;
            }

            if (item.getItemStack().getType().name().contains("_SWORD") && player.getInventory().getHeldItemSlot() == 0) {
                Language.MATCH_CANNOT_DROP_WEAPON.sendMessage(player);
                event.setCancelled(true);
                return;
            }

            if (item.getItemStack().getType() == Material.GLASS_BOTTLE) {
                item.remove();
                return;
            }

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
            MatchEntity entity = match.getEntities().stream().filter(matchEntity -> matchEntity.getEntity().getEntityId() == item.getEntityId()).findFirst().orElse(null);
            if (entity != null) {
                match.getEntities().remove(entity);
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
                    player.setItemInHand(new ItemBuilder(player.getItemInHand()).amount(player.getItemInHand().getAmount() - 1).build());
                    player.setHealth(20);
                    player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
                    if (Config.MATCH_GOLDEN_APPLE_INSTANT_GAPPLE_EFFECTS.toBoolean()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                        if (Config.MATCH_GOLDEN_APPLE_GIVE_ABSORPTION_HEARTS_EVERYTIME.toBoolean()) {
                            ((CraftPlayer) player).getHandle().setAbsorptionHearts(4);
                        }
                    } else {
                        ((CraftPlayer) player).getHandle().setAbsorptionHearts(0);
                    }
                    return;
                } else if (event.getItem().hasItemMeta() && ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).toLowerCase().contains("golden head")) {
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
                //Golden Head
                if (itemStack.getType() == Material.SKULL_ITEM && match.getKit().getGameRules().isHypixelUHC() && itemStack.hasItemMeta() && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).toLowerCase().contains("golden head")) {
                    if (!profile.getCooldowns().get(CooldownType.GOLDEN_HEAD).isExpired()) {
                        String time = TimeUtil.millisToSeconds(profile.getCooldowns().get(CooldownType.GOLDEN_HEAD).getRemaining());
                        Language.MATCH_USE_AGAIN_GOLDEN_HEAD.sendMessage(player, time);
                    } else {
                        profile.getCooldowns().put(CooldownType.GOLDEN_HEAD, new Cooldown(1));

                        EdenSound.GOLDEN_HEAD_EAT.play(player);
                        for (String s : Config.MATCH_GOLDEN_HEAD_EFFECTS.toStringList()) {
                            String[] effect = s.split(";");
                            PotionEffectType type = PotionEffectType.getByName(effect[0]);
                            int duration = Integer.parseInt(effect[1]);
                            int amplifier = Integer.parseInt(effect[2]);

                            player.removePotionEffect(type);
                            player.addPotionEffect(new PotionEffect(type, duration, amplifier));
                        }
                        player.setFoodLevel(Math.min(player.getFoodLevel() + Config.MATCH_GOLDEN_HEAD_FOOD_LEVEL.toInteger(), 20));
                        player.setItemInHand(new ItemBuilder(player.getItemInHand()).amount(player.getItemInHand().getAmount() - 1).build());
                        player.updateInventory();
                    }
                    //無論金頭顱食用結果如何, 都必須要 cancel event, 不然玩家就可以放置金頭顱在地上
                    event.setCancelled(true);
                    return;
                }
                //Ender Pearl
                else if (itemStack.getType() == Material.ENDER_PEARL && action.name().startsWith("RIGHT_")) {
                    Kit kit = match.getKit();
                    if (match.getState() == MatchState.STARTING && kit.getGameRules().isStartFreeze()) {
                        event.setCancelled(true);
                        return;
                    }
                    if (kit.getGameRules().isEnderPearlCooldown()) {
                        if (!profile.getCooldowns().get(CooldownType.ENDER_PEARL).isExpired()) {
                            String time = TimeUtil.millisToSeconds(profile.getCooldowns().get(CooldownType.ENDER_PEARL).getRemaining());
                            Language.MATCH_USE_AGAIN_ENDER_PEARL.sendMessage(player, time);
                            event.setCancelled(true);
                            return;
                        } else {
                            Util.throwEnderPearl(event); //Try to fix #514
                            profile.getCooldowns().put(CooldownType.ENDER_PEARL, new Cooldown(16) {
                                @Override
                                public void cancelCountdown() {
                                    super.cancelCountdown();

                                    player.setLevel(0);
                                    player.setExp(0);
                                }

                                @Override
                                public void runUnexpired() {
                                    int seconds = Math.round(profile.getCooldowns().get(CooldownType.ENDER_PEARL).getRemaining()) / 1000;

                                    player.setLevel(seconds);
                                    player.setExp(profile.getCooldowns().get(CooldownType.ENDER_PEARL).getRemaining() / 16000F);
                                }

                                @Override
                                public void runExpired() {
                                    Language.MATCH_CAN_USE_ENDERPEARL.sendMessage(player);
                                    if (player.getLevel() > 0) player.setLevel(0);
                                    if (player.getExp() > 0.0F) player.setExp(0.0F);
                                }
                            });
                            return;
                        }
                    }
                    return;
                }
                //Fireball
                else if (itemStack.getType() == Material.FIREBALL && action.name().startsWith("RIGHT_") && Config.MATCH_FIREBALL_ENABLED.toBoolean()) {
                    Kit kit = match.getKit();
                    if (match.getState() == MatchState.STARTING && kit.getGameRules().isStartFreeze()) {
                        event.setCancelled(true);
                        return;
                    }
                    if (!profile.getCooldowns().get(CooldownType.FIREBALL).isExpired()) {
                        String time = TimeUtil.millisToSeconds(profile.getCooldowns().get(CooldownType.FIREBALL).getRemaining());
                        Language.MATCH_USE_AGAIN_FIREBALL.sendMessage(player, time);
                    } else {
                        profile.getCooldowns().put(CooldownType.FIREBALL, new Cooldown(300L));

                        final Vector direction = player.getEyeLocation().getDirection();
                        final Fireball f = player.launchProjectile(Fireball.class);
                        FireballUtil.setDirection(f, direction);
                        f.setYield((float) Config.MATCH_FIREBALL_YIELD.toDouble());
                        f.setIsIncendiary(false);

                        itemStack.setAmount(itemStack.getAmount() - 1);
                        player.setItemInHand(itemStack);
                    }
                    event.setCancelled(true);
                    return;
                }
                //Soup
                else if (itemStack.getType() == Material.MUSHROOM_SOUP && player.getHealth() < 19.0) {
                    final double newHealth = Math.min(player.getHealth() + 7.0, 20.0);
                    player.setHealth(newHealth);
                    player.setFoodLevel(20);
                    player.getItemInHand().setType(Material.BOWL);
                    player.updateInventory();

                    event.setCancelled(true);
                    return;
                }
                //Kit Loadout Book
                else if (itemStack.getType() == Material.BOOK || itemStack.getType() == Material.ENCHANTED_BOOK) {
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

            match.getPlacedBlocks().add(block.getLocation());
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());

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

            match.getPlacedBlocks().remove(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getToBlock();
        if (block == null) {
            return;
        }

        for (Match match : Match.getMatches().values()) {
            ArenaDetail arenaDetail = match.getArenaDetail();
            if (arenaDetail == null) {
                return;
            }

            Cuboid cuboid = arenaDetail.getCuboid();
            Location blockLocation = block.getLocation();

            if (cuboid.contains(blockLocation)) {
                match.getPlacedBlocks().add(blockLocation);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        Block block = event.getBlockPlaced();

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
            if (match.isProtected(block.getLocation(), true, block)) {
                event.setCancelled(true);
                return;
            }

            if (block.getType() == Material.TNT && Config.MATCH_TNT_ENABLED.toBoolean()) {
                ItemStack itemStack = player.getItemInHand();
                itemStack.setAmount(itemStack.getAmount() - 1);
                player.setItemInHand(itemStack);

                final TNTPrimed tntPrimed = event.getBlock().getLocation().getWorld().spawn(event.getBlock().getLocation().clone().add(0.5, 0.0, 0.5), TNTPrimed.class);
                tntPrimed.setYield((float) Config.MATCH_TNT_YIELD.toDouble());
                tntPrimed.setFuseTicks(Config.MATCH_TNT_FUSE_TICKS.toInteger());
                Util.setSource(tntPrimed, player);

                event.setCancelled(true);
                return;
            }

            match.getPlacedBlocks().add(block.getLocation());

            if (match.getKit().getGameRules().isClearBlock()) {
                TeamPlayer teamPlayer = match.getTeamPlayer(player);

                new MatchClearBlockTask(match, match.getKit().getGameRules().getClearBlockTime(), block.getWorld(), block.getLocation(), teamPlayer, (itemStacks) -> {
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
            if (block.getType() == Material.BED_BLOCK) {
                //Now get the bed location
                Location bedLocation1 = new Location(block.getLocation().getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                Location bedLocation2 = Util.getBedBlockNearBy(bedLocation1).clone(); //因為一張床等於兩個方塊, 所以需要床的另一邊位置

                Team team = match.getTeam(player);
                Team opponentTeam = match.getTeams().stream().min(Comparator.comparing(t -> t.getSpawnLocation().distance(bedLocation1))).orElse(null);
                if (opponentTeam == null) {
                    throw new PracticeUnexpectedException("Cannot find the opponent team when player is destroying a bed (Match UUID: " + match.getUuid() + ")");
                }

                event.setCancelled(true);

                if (team == opponentTeam) {
                    Language.MATCH_CANNOT_BREAK_OWN_BED.sendMessage(player);
                    return;
                }

                if (kit.getGameRules().isBed()) {
                    match.broadcastSound(team, EdenSound.SELF_BREAK_BED);
                    match.broadcastSound(opponentTeam, EdenSound.OPPONENT_BREAK_BED);
                    match.broadcastSpectatorsSound(EdenSound.SELF_BREAK_BED);
                    match.broadcastTitle(opponentTeam, Language.MATCH_BED_BREAK_TITLE.toString());
                    match.broadcastSubTitle(opponentTeam, Language.MATCH_BED_BREAK_SUBTITLE.toString());
                    match.broadcastMessage(Language.MATCH_BED_BREAK_MESSAGE.toStringList(opponentTeam.getTeamColor().getTeamName(), team.getTeamColor().getColor(), player.getName()));
                    opponentTeam.setBedDestroyed(true);

                    bedLocation1.getBlock().setType(Material.AIR);
                    bedLocation2.getBlock().setType(Material.AIR);
                    return;
                } else if (kit.getGameRules().isBreakGoal()) {
                    match.score(profile, null, match.getTeamPlayer(player));
                    return;
                }
            }
            if (kit.getGameRules().isSpleef()) {
                if (block.getType() == Material.SNOW_BLOCK && player.getInventory().firstEmpty() != -1 && Config.MATCH_SNOW_SNOWBALL_DROP_CHANCE.toInteger() > ThreadLocalRandom.current().nextInt(0, 100)) {
                    player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, Config.MATCH_SNOW_SNOWBALL_DROP_AMOUNT.toInteger()));
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
                if (match.getState() == MatchState.STARTING && !Config.MATCH_ALLOW_PREFIRE.toBoolean() && projectile instanceof Arrow) {
                    event.setCancelled(true);
                    Language.MATCH_CANNOT_PREFIRE.sendMessage(player);
                    Tasks.runLater(() -> Util.giveBackArrow(match, player), 1L);
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
                match.getEntities().add(new MatchEntity(match, projectile));
                if (projectile instanceof ThrownPotion) {
                    match.getTeamPlayer(player).addPotionsThrown();
                } else if (projectile instanceof Arrow && match.getKit().getGameRules().isGiveBackArrow()) {
                    profile.getCooldowns().put(CooldownType.ARROW, new Cooldown(3500L) {
                        @Override
                        public void runUnexpired() {
                            int seconds = Math.round(profile.getCooldowns().get(CooldownType.ARROW).getRemaining()) / 1000;

                            player.setLevel(seconds);
                            player.setExp(profile.getCooldowns().get(CooldownType.ARROW).getRemaining() / 3500F);
                        }
                        @Override
                        public void runExpired() {
                            if (!player.getInventory().contains(Material.ARROW)) {
                                Util.giveBackArrow(match, player);
                            }
                            if (player.getLevel() > 0) player.setLevel(0);
                            if (player.getExp() > 0.0F) player.setExp(0.0F);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
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

                if (event.getEntityType() == EntityType.SNOWBALL) {
                    Location location = event.getEntity().getLocation().clone().add(0, -1, 0);
                    if (location.getBlock().getType() == Material.SNOW_BLOCK && match.getKit().getGameRules().isSpleef()) {
                        location.getBlock().setType(Material.AIR);
                    }
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
    public void onExplode(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        Location location = event.getLocation();
        Match match = Match.getMatches().values().stream().filter(m -> m.getArenaDetail().getCuboid().contains(location)).findFirst().orElse(null);
        if (match == null) {
            Common.log(CC.RED + "ERROR: Cannot find match when explosion happens (" + LocationSerialization.toReadable(location) + CC.RED + ") (" + type.name() + ")");
            return;
        }

        if (type == EntityType.FIREBALL && Config.MATCH_FIREBALL_ENABLED.toBoolean()) {
            event.blockList().removeIf(block -> !Config.MATCH_FIREBALL_ALLOWED_BREAKING_BLOCKS.toStringList().contains(block.getType().name()) || match.isProtected(block.getLocation(), false) || block.getType() == Material.BED_BLOCK);
        } else if (type == EntityType.PRIMED_TNT && Config.MATCH_TNT_ENABLED.toBoolean()) {
            event.blockList().removeIf(block -> !Config.MATCH_TNT_ALLOWED_BREAKING_BLOCKS.toStringList().contains(block.getType().name()) || match.isProtected(block.getLocation(), false) || block.getType() == Material.BED_BLOCK);
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
                    player.setItemOnCursor(null); //Fix for #308 point 1 - Prevent book duplicate
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
