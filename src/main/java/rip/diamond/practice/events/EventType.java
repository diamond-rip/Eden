package rip.diamond.practice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import rip.diamond.practice.Language;
import rip.diamond.practice.events.impl.Tournament;

@Getter
@AllArgsConstructor
public enum EventType {

    TOURNAMENT(Material.DIAMOND_SWORD, Language.EVENT_TOURNAMENT_NAME.toString(), 2, 64, "eden.event.tournament", true, true, Tournament.class),
    ;

    private final Material logo;
    private final String name;
    private final int defaultMinPlayers;
    private final int defaultMaxPlayers;
    private final String permission;
    private final boolean kit;
    private final boolean allowTeams;
    private final Class<?> clazz;

}
