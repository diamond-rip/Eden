package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class MatchEndEvent extends BaseEvent {

    private final Match match;
    private final boolean forceEnd;

}
