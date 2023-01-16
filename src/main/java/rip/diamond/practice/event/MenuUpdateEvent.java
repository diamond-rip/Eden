package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.util.BaseEvent;
import rip.diamond.practice.util.menu.Menu;

@Getter
@RequiredArgsConstructor
public class MenuUpdateEvent extends BaseEvent {

    private final Menu menu;

}
