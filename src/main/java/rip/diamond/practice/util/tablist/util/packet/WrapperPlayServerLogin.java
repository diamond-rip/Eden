package rip.diamond.practice.util.tablist.util.packet;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;

public class WrapperPlayServerLogin extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.LOGIN;
    
    public WrapperPlayServerLogin() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerLogin(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the player's Entity ID.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the player's Entity ID.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the player's entity object.
     * @param world - the word the player has joined.
     * @return The player's entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the player's entity object.
     * @param event - the packet event.
     * @return The player's entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve the game mode of the initial world.
     * @return The current gamemode.
    */
    public NativeGameMode getGamemode() {
        return handle.getGameModes().read(0);
    }
    
    /**
     * Set the game mode of the initial world.
     * @param value - new value.
    */
    public void setGamemode(NativeGameMode value) {
        handle.getGameModes().write(0, value);
    }
    
    /**
     * Retrieve whether or not this is a hardcore world.
     * @return TRUE if it is, FALSE otherwise.
    */
    public boolean isHardcore() {
        return handle.getBooleans().read(0);
    }
    
    /**
     * Set whether or not this is a hardcore world.
     * @param value - TRUE if it is, FALSE otherwise.
    */
    public void setHardcore(boolean value) {
        handle.getBooleans().write(0, value);
    }
    
    /**
     * Retrieve -1: nether, 0: overworld, 1: end.
     * @return The current Dimension
    */
    public int getDimension() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set -1: nether, 0: overworld, 1: end.
     * @param value - new value.
    */
    public void setDimension(int value) {
    	handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve the difficulty of the initial world.
     * @return The current difficulty
    */
    public Difficulty getDifficulty() {
        return handle.getDifficulties().read(0);
    }
    
    /**
     * Set the difficulty of the initial world.
     * @param difficulty - new difficulty.
    */
    public void setDifficulty(Difficulty difficulty) {
        handle.getDifficulties().write(0, difficulty);
    }
    
    /**
     * Retrieve the maximum number of players.
     * <p>
     * This is used by the client to draw the player list.
     * @return The current max players.
    */
    public byte getMaxPlayers() {
        return handle.getIntegers().read(2).byteValue();
    }
    
    /**
     * Set used by the client to draw the player list.
     * @param value - new value.
    */
    public void setMaxPlayers(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the world type.
     * <p>
     * This is the level-type setting (default, flat, or largeBiomes) in server.properties.
     * @return The current world type.
    */
    public WorldType getLevelType() {
        return handle.getWorldTypeModifier().read(0);
    }
    
    /**
     * Set the world type.
     * <p>
     * This is the level-type setting (default, flat, or largeBiomes) in server.properties.
     * @param type - new value.
    */
    public void setLevelType(WorldType type) {
        handle.getWorldTypeModifier().write(0, type);
    }    
}

