package rip.diamond.practice.profile.task;

import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.cooldown.Cooldown;
import rip.diamond.practice.profile.cooldown.CooldownType;
import rip.diamond.practice.util.TaskTicker;

import java.util.Map;

public class ProfileCooldownTask extends TaskTicker {
    public ProfileCooldownTask() {
        super(0, 1, true);
    }

    @Override
    public synchronized void onRun() {
        for (PlayerProfile profile : PlayerProfile.getProfiles().values()) {
            for (Map.Entry<CooldownType, Cooldown> cooldownTypeCooldownEntry : profile.getCooldowns().entrySet()) {
                Cooldown cooldown = cooldownTypeCooldownEntry.getValue();
                //expire == 0 means the cooldown is cancelled by other stuff
                if (cooldown.getExpire() != 0) {
                    cooldown.run();
                }
            }
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
