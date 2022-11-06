/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package rip.diamond.practice.util.tablist.util.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;

import java.util.Collection;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;
    
    /**
     * Enumeration of all the known packet modes.
     * 
     * @author Kristian
     */
    public static class Modes extends IntEnum {
    	public static final int TEAM_CREATED = 0;
    	public static final int TEAM_REMOVED = 1;
    	public static final int TEAM_UPDATED = 2;
    	public static final int PLAYERS_ADDED = 3;
    	public static final int PLAYERS_REMOVED = 4;
    	
    	private static final Modes INSTANCE = new Modes();
    	
    	public static Modes getInstance() {
    		return INSTANCE;
    	}
    }
    
    public WrapperPlayServerScoreboardTeam() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve an unique name for the team. (Shared with scoreboard)..
     * @return The current Team Name
    */
    public String getTeamName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set an unique name for the team. (Shared with scoreboard)..
     * @param value - new value.
    */
    public void setTeamName(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve the current packet {@link Modes}.
     * <p>
     * This determines whether or not team information is added or removed.
     * @return The current packet mode.
    */
    public byte getPacketMode() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the current packet {@link Modes}.
     * <p>
     * This determines whether or not team information is added or removed.
     * @param value - new value.
    */
    public void setPacketMode(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the team display name.
     * <p>
     * A team must be created or updated.
     * @return The current display name.
    */
    public String getTeamDisplayName() {
        return handle.getStrings().read(1);
    }
    
    /**
     * Set the team display name.
     * <p>
     * A team must be created or updated.
     * @param value - new value.
    */
    public void setTeamDisplayName(String value) {
    	handle.getStrings().write(1, value);
    }
    
    /**
     * Retrieve the team prefix. This will be inserted before the name of each team member.
     * <p>
     * A team must be created or updated.
     * @return The current Team Prefix
    */
    public String getTeamPrefix() {
        return handle.getStrings().read(2);
    }
    
    /**
     * Set the team prefix. This will be inserted before the name of each team member.
     * <p>
     * A team must be created or updated.
     * @param value - new value.
    */
    public void setTeamPrefix(String value) {
        handle.getStrings().write(2, value);
    }
    
    /**
     * Set the team suffix. This will be inserted after the name of each team member.
     * <p>
     * A team must be created or updated.
     * @return The current Team Suffix
    */
    public String getTeamSuffix() {
        return handle.getStrings().read(3);
    }
    
    /**
     * Set only if Mode = 0 or 2. This will be after before the name of each team member.
     * <p>
     * A team must be created or updated.
     * @param value - new value.
    */
    public void setTeamSuffix(String value) {
        handle.getStrings().write(3, value);
    }
    
    /**
     * Retrieve whether or not friendly fire is enabled.
     * <p>
     * A team must be created or updated.
     * @return The current Friendly fire
    */
    public byte getFriendlyFire() {
        return handle.getIntegers().read(1).byteValue();
    }
    
    /**
     * Set whether or not friendly fire is enabled.
     * <p>
     * A team must be created or updated.
     * @param value - new value.
    */
    public void setFriendlyFire(byte value) {
    	handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the list of player names.
     * <p>
     * Packet mode must be one of the following for this to be valid:
     * <ul>
     *  <li>{@link Modes#TEAM_CREATED}</li>
     *  <li>{@link Modes#PLAYERS_ADDED}</li>
     *  <li>{@link Modes#PLAYERS_REMOVED}</li>
     * </ul>
     * @return A list of player names.
    */
    @SuppressWarnings("unchecked")
	public Collection<String> getPlayers() {
        return handle.getSpecificModifier(Collection.class).read(0);
    }
    
    /**
     * Set the list of player names.
     * <p>
     * Packet mode must be one of the following for this to be valid:
     * <ul>
     *  <li>{@link Modes#TEAM_CREATED}</li>
     *  <li>{@link Modes#PLAYERS_ADDED}</li>
     *  <li>{@link Modes#PLAYERS_REMOVED}</li>
     * </ul>
     * @param players - new players.
    */
    public void setPlayers(Collection<String> players) {
    	handle.getSpecificModifier(Collection.class).write(0, players);
    }
}