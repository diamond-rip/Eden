package rip.diamond.practice.util.tablist.client;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class ClientVersionUtil {

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

}
