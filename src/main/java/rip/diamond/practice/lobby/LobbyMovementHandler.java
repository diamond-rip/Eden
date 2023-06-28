package rip.diamond.practice.lobby;

import rip.diamond.practice.Eden;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;

public class LobbyMovementHandler {

    public LobbyMovementHandler(Eden plugin) {
        Eden.INSTANCE.getSpigotAPI().getMovementHandler().injectLocationUpdate((player, from, to) -> {
            PlayerProfile profile = PlayerProfile.get(player);

            if (profile != null && (profile.getPlayerState() == PlayerState.IN_LOBBY || profile.getPlayerState() == PlayerState.IN_QUEUE) && player.getLocation().getY() < 0) {
                plugin.getLobbyManager().teleport(player);
            }
        });
    }

}
