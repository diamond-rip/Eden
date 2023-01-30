package rip.diamond.practice.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import rip.diamond.practice.Eden;
import rip.diamond.practice.profile.PlayerProfile;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static com.comphenix.protocol.PacketType.Play.Server.*;

/**
 * Entity hider which aims to fix spigot visibility
 * of projectiles, particle effects and sounds
 * <p>
 * Originally coded by Lipchya and cleaned/improved and maintained by DevDrizzy
 * Now maintained by GoodestEnglish to make this class fit this project
 *
 * @since 9/13/2021
 * Project: Eden
 */

public class EntityHider {

    protected Table<Integer, Integer, Boolean> observerEntityMap = HashBasedTable.create();

    public Field itemOwner;
    private final PacketType[] ENTITY_PACKETS = {
            ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
            COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
            ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_TELEPORT,
            ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
            ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION,
            WORLD_EVENT,
            NAMED_SOUND_EFFECT
    };

    public enum Policy {
        /**
         * All entities are invisible by default. Only entities specifically made visible may be seen.
         */
        WHITELIST,

        /**
         * All entities are visible by default. An entity can only be hidden explicitly.
         */
        BLACKLIST,
    }

    private final ProtocolManager manager;
    private final Eden plugin;

    private final Listener bukkitListener;
    private final PacketAdapter protocolListener;

    protected final Policy policy;

    public EntityHider(Eden plugin, Policy policy) {
        Preconditions.checkNotNull(plugin, "plugin cannot be NULL.");

        this.plugin = plugin;
        this.policy = policy;
        this.manager = ProtocolLibrary.getProtocolManager();

        this.bukkitListener = constructBukkit();
        this.protocolListener = constructProtocol(plugin);
    }

    @SneakyThrows
    public EntityHider init() {
        manager.addPacketListener(protocolListener);
        plugin.getServer().getPluginManager().registerEvents(bukkitListener, plugin);

        itemOwner = EntityItem.class.getDeclaredField("f");
        itemOwner.setAccessible(true);

        return this;
    }

    /**
     * Set the visibility status of a given entity for a particular observer.
     * @param observer - the observer player.
     * @param entityID - ID of the entity that will be hidden or made visible.
     * @param visible - TRUE if the entity should be made visible, FALSE if not.
     * @return TRUE if the entity was visible before this method call, FALSE otherwise.
     */
    protected boolean setVisibility(Player observer, int entityID, boolean visible) {
        switch (policy) {
            case BLACKLIST:
                // Non-membership means they are visible
                return !setMembership(observer, entityID, !visible);
            case WHITELIST:
                return setMembership(observer, entityID, visible);
            default:
                throw new IllegalArgumentException("Unknown policy: " + policy);
        }
    }

    /**
     * Add or remove the given entity and observer entry from the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @param member   - TRUE if they should be present in the table, FALSE otherwise.
     * @return TRUE if they already were present, FALSE otherwise.
     */
    // Helper method
    protected boolean setMembership(Player observer, int entityID, boolean member) {
        if (member) {
            return observerEntityMap.put(observer.getEntityId(), entityID, true) != null;
        } else {
            return observerEntityMap.remove(observer.getEntityId(), entityID) != null;
        }
    }

    /**
     * Determine if the given entity and observer is present in the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @return TRUE if they are present, FALSE otherwise.
     */
    protected boolean getMembership(Player observer, int entityID) {
        return observerEntityMap.contains(observer.getEntityId(), entityID);
    }

    /**
     * Determine if a given entity is visible for a particular observer.
     *
     * @param observer - the observer player.
     * @param entityID -  ID of the entity that we are testing for visibility.
     * @return TRUE if the entity is visible, FALSE otherwise.
     */
    protected boolean isVisible(Player observer, int entityID) {
        // If we are using a whitelist, presence means visibility - if not, the opposite is the case

        boolean presence = getMembership(observer, entityID);

        return (policy == Policy.WHITELIST) == presence;
    }

    /**
     * Remove the given entity from the underlying map.
     *
     * @param entity - the entity to remove.
     */
    protected void removeEntity(Entity entity) {
        int entityID = entity.getEntityId();

        for (Map<Integer, Boolean> maps : observerEntityMap.rowMap().values()) {
            maps.remove(entityID);
        }
    }

    /**
     * Invoked when a player logs out.
     *
     * @param player - the player that used logged out.
     */
    protected void removePlayer(Player player) {
        // Cleanup
        observerEntityMap.rowMap().remove(player.getEntityId());
    }

    /**
     * Construct the Bukkit event listener.
     *
     * @return Our listener.
     */
    private Listener constructBukkit() {
        return new Listener() {
            @EventHandler
            public void onEntityDeath(EntityDeathEvent e) {
                removeEntity(e.getEntity());
            }

            @EventHandler
            public void onChunkUnload(ChunkUnloadEvent e) {
                for (Entity entity : e.getChunk().getEntities()) {
                    removeEntity(entity);
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                removePlayer(e.getPlayer());
            }


            @EventHandler(priority = EventPriority.MONITOR)
            public void onPotionSplash(PotionSplashEvent event) {
                ThrownPotion potion = event.getEntity();
                if (!(potion.getShooter() instanceof Player)) return;

                Player shooter = (Player) potion.getShooter();

                for (LivingEntity livingEntity : event.getAffectedEntities()) {
                    if (!(livingEntity instanceof Player)) return;

                    Player receiver = (Player) livingEntity;
                    if (!receiver.canSee(shooter)) {
                        event.setIntensity(receiver, 0.0D);
                    }
                }
            }
        };
    }

    /**
     * Construct the packet listener that will be used to intercept every entity-related packet.
     *
     * @param plugin - the parent plugin.
     * @return The packet listener.
     */
    private PacketAdapter constructProtocol(Plugin plugin) {
        return new PacketAdapter(plugin, ENTITY_PACKETS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPlayer() == null) return;
                if (event.getPacket() == null) return;
                
                int entityID = event.getPacket().getIntegers().read(0);

                // See if this packet should be cancelled
                if (!isVisible(event.getPlayer(), entityID)) {
                    event.setCancelled(true);
                    return;
                }

                PacketType type = event.getPacketType();
                Player receiver = event.getPlayer();

                if (type == WORLD_EVENT) {
                    int effect = event.getPacket().getIntegers().read(0);
                    if (effect != 2002) return;

                    BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);

                    int x = position.getX();
                    int y = position.getY();
                    int z = position.getZ();

                    boolean isVisible = false;
                    boolean isInMatch = false;

                    for ( ThrownPotion potion : receiver.getWorld().getEntitiesByClass(ThrownPotion.class) ) {
                        int potionX = MathHelper.floor(x);
                        int potionY = MathHelper.floor(y);
                        int potionZ = MathHelper.floor(z);

                        if (!(potion.getShooter() instanceof Player)) continue;
                        if (x != potionX || y != potionY || z != potionZ) continue;

                        isInMatch = true;
                        Player shooter = (Player) potion.getShooter();
                        if (receiver.canSee(shooter))
                            isVisible = true;
                    }

                    if (isInMatch && !isVisible) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (type == NAMED_SOUND_EFFECT) {
                    String sound = event.getPacket().getStrings().read(0);
                    
                    if (sound.equals("random.bow") || sound.equals("random.bowhit") || sound.equals("random.pop") || sound.equals("game.player.hurt")) {
                        int x = event.getPacket().getIntegers().read(0);
                        int y = event.getPacket().getIntegers().read(1);
                        int z = event.getPacket().getIntegers().read(2);

                        //Credit: Array Practice
                        boolean hasAnyPlayable = false;
                        boolean hasAtleastOneMatch = false;

                        for (Entity entity : receiver.getWorld().getEntitiesByClasses(Player.class , Projectile.class)) {
                            Player player;
                            if (entity instanceof Player) {
                                player = (Player) entity;
                            } else if (entity instanceof Projectile) {
                                Projectile projectile = (Projectile) entity;
                                if (projectile.getShooter() instanceof Player) {
                                    player = (Player) projectile.getShooter();
                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                            Location location = entity.getLocation();
                            if(((int)(location.getX() * 8.0D) == x) && ((int)(location.getY() * 8.0D) == y) && ((int)(location.getZ() * 8.0D) == z)) {
                                boolean pass = false;
                                if(sound.equals("random.bow")) {
                                    ItemStack hand = player.getItemInHand();
                                    if(hand != null) {
                                        if(hand.getType() == Material.POTION || hand.getType() ==  Material.BOW || hand.getType() == Material.ENDER_PEARL) {
                                            pass = true;
                                        }
                                    }
                                }else if(sound.equals("random.bowhit")) {
                                    if(entity instanceof Arrow) {
                                        pass = true;
                                    }
                                }else {
                                    if(entity instanceof Player) {
                                        pass = true;
                                    }
                                }
                                if(pass) {
                                    hasAtleastOneMatch = true;
                                    if(receiver.canSee(player)) {
                                        hasAnyPlayable = true;
                                    }
                                }
                            }
                        }
                        if(hasAtleastOneMatch && !hasAnyPlayable) {
                            event.setCancelled(true);
                        }
                    }
                } else {
                    Entity entity = receiver.getWorld().getEntities().stream().filter(e -> e.getEntityId() == entityID).findFirst().orElse(null);

                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        if (receiver.canSee(player)) return;

                        event.setCancelled(true);
                        return;
                    } else if (entity instanceof Projectile) {
                        Projectile projectile = (Projectile) entity;
                        if (!(projectile.getShooter() instanceof Player)) return;

                        Player shooter = (Player) projectile.getShooter();
                        if (receiver.canSee(shooter)) return;

                        event.setCancelled(true);
                        return;
                    } else if (entity instanceof Item) {
                        Item item = (Item) entity;

                        Player dropper = getPlayerWhoDropped(item);
                        if (dropper == null) return;
                        if (receiver.canSee(dropper)) return;
                        if (PlayerProfile.get(receiver) != null && PlayerProfile.get(dropper) != null && PlayerProfile.get(receiver).getMatch() == PlayerProfile.get(dropper).getMatch()) return;

                        event.setCancelled(true);
                        return;
                    }
                }
            }
        };
    }

    /**
     * Toggle the visibility status of an entity for a player.
     * <p>
     * If the entity is visible, it will be hidden. If it is hidden, it will become visible.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to toggle.
     * @return TRUE if the entity was visible before, FALSE otherwise.
     */
    public final boolean toggleEntity(Player observer, Entity entity) {
        if (isVisible(observer, entity.getEntityId())) {
            return hideEntity(observer, entity);
        } else {
            return !showEntity(observer, entity);
        }
    }

    /**
     * Allow the observer to see an entity that was previously hidden.
     *
     * @param observer - the observer.
     * @param entity   - the entity to show.
     * @return TRUE if the entity was hidden before, FALSE otherwise.
     */
    public final boolean showEntity(Player observer, Entity entity) {
        validate(observer, entity);
        boolean hiddenBefore = !setVisibility(observer, entity.getEntityId(), true);

        // Resend packets
        if (manager != null && hiddenBefore) {
            manager.updateEntity(entity, Collections.singletonList(observer));
        }
        return hiddenBefore;
    }

    /**
     * Prevent the observer from seeing a given entity.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to hide.
     * @return TRUE if the entity was previously visible, FALSE otherwise.
     */
    public final boolean hideEntity(Player observer, Entity entity) {
        validate(observer, entity);
        boolean visibleBefore = setVisibility(observer, entity.getEntityId(), false);

        if (visibleBefore) {
            // Make the entity disappear
            try {
                destroy(observer, entity.getEntityId());
            } catch (Exception e) {
                throw new RuntimeException("Cannot send server packet.", e);
            }

        }
        return visibleBefore;
    }

    /**
     * Determine if the given entity has been hidden from an observer.
     * Note that the entity may very well be occluded or out of range from the perspective
     * of the observer. This method simply checks if an entity has been completely hidden
     * for that observer.
     *
     * @param observer - the observer.
     * @param entity   - the entity that may be hidden.
     * @return TRUE if the player may see the entity, FALSE if the entity has been hidden.
     */
    public final boolean canSee(Player observer, Entity entity) {
        validate(observer, entity);

        return isVisible(observer, entity.getEntityId());
    }

    // For validating the input parameters
    private void validate(Player observer, Entity entity) {
        Preconditions.checkNotNull(observer, "observer cannot be NULL.");
        Preconditions.checkNotNull(entity, "entity cannot be NULL.");
    }

    /**
     * Retrieve the current visibility policy.
     *
     * @return The current visibility policy.
     */
    public Policy getPolicy() {
        return policy;
    }

    public void close() {
        HandlerList.unregisterAll(bukkitListener);
        manager.removePacketListener(protocolListener);
        itemOwner.setAccessible(false);
    }

    private Player getPlayerWhoDropped(Item item) {
        try {
            String name = (String) itemOwner.get(((CraftEntity)item).getHandle());
            if (name == null) return null;
            return Bukkit.getPlayer(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void setPlayerWhoDropped(Item item, String owner) {
        setPlayerWhoDropped(((CraftEntity)item).getHandle(), owner);
    }

    public void setPlayerWhoDropped(net.minecraft.server.v1_8_R3.Entity entity, String owner) {
        try {
            itemOwner.set(entity, owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy(Player player, int entityId) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}