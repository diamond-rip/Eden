package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class PlayerProfileDataSaveEvent extends BaseEvent {

    private final PlayerProfile profile;
    private final Document document;

}
