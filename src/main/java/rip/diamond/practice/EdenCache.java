package rip.diamond.practice;

import lombok.Getter;
import org.bukkit.Bukkit;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.TaskTicker;

@Getter
public class EdenCache {

    private int playersSize;
    private int queuePlayersSize;
    private int matchPlayersSize;

    public EdenCache() {
        new TaskTicker(0, 5, true) {
            @Override
            public void onRun() {
                playersSize = Bukkit.getOnlinePlayers().size();
                queuePlayersSize = (int) PlayerProfile.getProfiles().values().stream().filter(p -> p.getPlayerState() == PlayerState.IN_QUEUE).count();
                matchPlayersSize = (int) PlayerProfile.getProfiles().values().stream().filter(p -> p.getPlayerState() == PlayerState.IN_MATCH || p.getPlayerState() == PlayerState.IN_SPECTATING).count();
            }

            @Override
            public TickType getTickType() {
                return TickType.NONE;
            }

            @Override
            public int getStartTick() {
                return 0;
            }
        };
    }

}
