package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class PartyDisbandEvent extends BaseEvent {

    private final Party party;
    private final boolean forced;

}
