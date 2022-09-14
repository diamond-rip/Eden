package rip.diamond.practice.profile.task;

import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.TaskTicker;

public class ProfileAutoSaveTask extends TaskTicker {
    public ProfileAutoSaveTask() {
        super(0, 20*60*5, true);
    }

    @Override
    public void onRun() {
        PlayerProfile.getProfiles().values().forEach(playerProfile -> {
            playerProfile.save(true, (success) -> {
                if (success && playerProfile.getPlayer() == null) {
                    PlayerProfile.getProfiles().remove(playerProfile.getUniqueId());
                }
            });
        });
    }

    @Override
    public void preRun() {

    }

    @Override
    public TickType getTickType() {
        return TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }
}
