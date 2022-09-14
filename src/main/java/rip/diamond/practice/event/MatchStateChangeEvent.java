package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
@RequiredArgsConstructor
public class MatchStateChangeEvent extends BaseEvent {

    private final Match match;

}
