package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.BaseEvent;
import rip.diamond.practice.util.menu.Menu;

@Getter
@RequiredArgsConstructor
public class SettingsMenuOpenEvent extends BaseEvent {

    private final Player player;
    private final Menu menu;

}
