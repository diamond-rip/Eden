package rip.diamond.practice.profile.task;

import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Cooldown;
import rip.diamond.practice.util.TaskTicker;

import java.util.Iterator;
import java.util.Map;

public class ProfileCooldownTask extends TaskTicker {
    public ProfileCooldownTask() {
        super(0, 1, true);
    }

    @Override
    public void onRun() {
        for (PlayerProfile profile : PlayerProfile.getProfiles().values()) {
            Iterator<Map.Entry<String, Cooldown>> cooldownIterator = profile.getCooldowns().entrySet().iterator();
            while (cooldownIterator.hasNext()) {
                Cooldown cooldown = cooldownIterator.next().getValue();
                //expire == 0 means the cooldown is cancelled by other stuff
                if (cooldown.getExpire() != 0) {
                    cooldown.run();
                }
                if (cooldown.isExpired()) {
                    cooldownIterator.remove();
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
