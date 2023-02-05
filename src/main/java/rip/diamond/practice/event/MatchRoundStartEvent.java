package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.util.BaseEvent;

@Getter
@Setter
@RequiredArgsConstructor
public class MatchRoundStartEvent extends BaseEvent {

    private final Match match;
    
}
