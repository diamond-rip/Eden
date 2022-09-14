package io.github.epicgo.sconey.reflection.impl;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A simple instance class PacketPlayOutScoreboardTeam via reflection
 */
@Getter
@AllArgsConstructor
public class RPacketScoreboardTeam extends Reflection {

    /**
     * The teamName to update the score objective
     */
    private final String teamName;
    /**
     * The prefix to update the score team
     */
    private final String prefix;
    /**
     * The suffix to update the score team
     */
    private final String suffix;
    /**
     * The optionData to update the score team
     */
    private final int optionData;
    /**
     * The playerNameSet to update the score team
     */
    private final List<String> playerNameSet;

    public void sendPacket(final Player player) {
        final Object packet = constructor(getNMSClass("PacketPlayOutScoreboardTeam"));
        if(teamName != null)
            setDeclaredField(packet, "a", teamName);
        if(prefix != null)
            setDeclaredField(packet, "c", prefix);
        if(suffix != null)
            setDeclaredField(packet, "d", suffix);

        if(isNative17()) {
            setDeclaredField(packet, "f", optionData);
            setDeclaredField(packet, "e", playerNameSet);
        } else {
            setDeclaredField(packet, "h", optionData);
            setDeclaredField(packet, "g", playerNameSet);
        }

        final Object craftPlayerHandle = method(player, "getHandle");
        final Object playerConnection = getField(craftPlayerHandle, "playerConnection");

        method(playerConnection, "sendPacket", packet);
    }
}
