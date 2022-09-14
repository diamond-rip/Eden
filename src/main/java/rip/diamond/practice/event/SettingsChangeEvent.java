package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class SettingsChangeEvent extends BaseEvent {

    private final Player player;
    private final PlayerProfile profile;
    private final ProfileSettings settings;

}
