package io.github.epicgo.sconey.reflection.impl;

import io.github.epicgo.sconey.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * A simple instance class PacketPlayOutScoreboardScore via reflection
 */
@Getter
@AllArgsConstructor
public class RPacketScoreboardScore extends Reflection {

    /**
     * The teamName to update the score objective
     */
    private final String teamName;
    /**
     * The objectiveName to set for specific team
     */
    private final String objectiveName;
    /**
     * The score to set position of scoreboard index
     */
    private final int score;
    /**
     * The enumScoreAction enum CHANGE/REMOVE
     */
    private final EnumScoreAction enumScoreAction;

    public void sendPacket(final Player player) {
        final Object packet = constructor(getNMSClass("PacketPlayOutScoreboardScore"));

        if (teamName != null)
            setDeclaredField(packet, "a", teamName);
        if (objectiveName != null)
            setDeclaredField(packet, "b", objectiveName);

        if (isNative17()) {
            setDeclaredField(packet, "d", enumScoreAction.ordinal());
        } else {
            final Class<?> enumScoreboardAction = getNMSClass("PacketPlayOutScoreboardScore$EnumScoreboardAction");
            setDeclaredField(packet, "d", method(enumScoreboardAction, "valueOf", enumScoreAction.name()));
        }

        setDeclaredField(packet, "c", score);

        final Object craftPlayerHandle = method(player, "getHandle");
        final Object playerConnection = getField(craftPlayerHandle, "playerConnection");

        method(playerConnection, "sendPacket", packet);
    }

    public enum EnumScoreAction {
        CHANGE, REMOVE
    }
}
