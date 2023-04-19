package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.diamond.practice.util.BaseEvent;
import rip.diamond.practice.util.menu.Menu;

@Getter
@RequiredArgsConstructor
public class MenuOpenEvent extends BaseEvent {

    private final Menu menu;

    public MenuOpenEvent(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null.");
        }
        this.menu = menu;
    }

    public MenuOpenEvent() {
        this.menu = null;
    }

}
