package rip.diamond.practice.profile.task;

import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.TaskTicker;

public class ProfileCooldownTask extends TaskTicker {
    public ProfileCooldownTask() {
        super(0, 1, true);
    }

    @Override
    public void onRun() {
        for (PlayerProfile profile : PlayerProfile.getProfiles().values()) {
            profile.getCooldowns().entrySet().removeIf(entry -> entry.getValue().isExpired());
        }
    }

    @Override
    public void preRun() {

    }

    @Override
    public TaskTicker.TickType getTickType() {
        return TaskTicker.TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }
}
