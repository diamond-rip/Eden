package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class EventJoinEvent extends BaseEvent {

    private final Party party;
    private final EdenEvent event;

}
