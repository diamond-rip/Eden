package rip.diamond.practice.lobby;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.PlayerUtil;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.serialization.LocationSerialization;

@Getter
public class LobbyManager {

    private final Eden plugin;

    private Location spawnLocation = null;

    public LobbyManager(Eden plugin) {
        this.plugin = plugin;
        try {
            this.spawnLocation = LocationSerialization.deserializeLocation(plugin.getLocationFile().getString("spawn-location"));
        } catch (Exception e) {
            Common.log("Unable to deserialize spawn-location from location file.");
        }
    }

    public void setSpawnLocation(Player player) {
        spawnLocation = player.getLocation();
        plugin.getLocationFile().getConfiguration().set("spawn-location", LocationSerialization.serializeLocation(spawnLocation));
        plugin.getLocationFile().save();

        Language.LOBBY_CHANGED_SPAWN_LOCATION.sendMessage(player);
    }

    private void teleport(Player player, Location location) {
        if (location == null) {
            Common.sendMessage(player, CC.RED + "Unable to teleport you to a certain location. Please check if spawn location and editor location is setup correctly.");
            Common.log(CC.RED + "Unable to teleport " + player.getName() + " to a certain location. Please check if spawn location and editor location is setup correctly.");
            return;
        }
        player.teleport(location);
    }

    public void sendToSpawnAndReset(Player player) {
        Tasks.run(()-> {
            PlayerProfile profile = PlayerProfile.get(player);

            if (profile == null) {
                return;
            }

            PlayerUtil.reset(player);

            profile.setMatch(null);
            profile.setPlayerState(PlayerState.IN_LOBBY);
            profile.setupItems();
            profile.getCooldowns().forEach((name, cooldown) -> {
                cooldown.cancelCountdown();
            });

            teleport(player, spawnLocation);
        });
    }

}