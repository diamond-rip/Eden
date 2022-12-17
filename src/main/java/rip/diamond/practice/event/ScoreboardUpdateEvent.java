package rip.diamond.practice.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.BaseEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ScoreboardUpdateEvent extends BaseEvent {

    private final Player player;
    private List<String> layout = new ArrayList<>();

}
