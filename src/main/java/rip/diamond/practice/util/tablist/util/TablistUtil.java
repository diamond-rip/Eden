package rip.diamond.practice.util.tablist.util;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.tablist.util.packet.WrapperPlayServerScoreboardTeam;

import java.util.Collection;

public class TablistUtil {

    /**
     * Get the protocol version of the client.
     * <br>
     * Had to be made because 1.8+ doesn't have a NetworkManager#getVersion method, which is required for legacy support on tab.
     *
     * @param player the player to get the version of
     * @return the version, or -1 if none of the plugins are supported.
     */
    public static int getProtocolVersion(Player player) {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.getPlugin("ViaVersion") != null) {
            return Via.getAPI().getPlayerVersion(player.getUniqueId());
        } /*else if(pluginManager.getPlugin("ProtocolSupport") != null) {
            return ProtocolSupportAPI.getProtocolVersion(player).getId();
        }*/

        return -1;
    }

    public static void sendTeam(Player player, String name, String prefix, String suffix, Collection<String> nameSet, int type) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();

        packet.setTeamName(name);
        packet.setTeamDisplayName("");
        packet.setTeamPrefix(prefix);
        packet.setTeamSuffix(suffix);
        packet.setPlayers(nameSet);
        packet.setPacketMode(type);

        packet.sendPacket(player);
    }

    public static int getPossibleSlots(Player player) {
        return getProtocolVersion(player) == 4 || getProtocolVersion(player) == 5 ? 60 : 80;
    }

}
