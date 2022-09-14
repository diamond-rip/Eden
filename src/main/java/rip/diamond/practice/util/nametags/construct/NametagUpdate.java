package rip.diamond.practice.util.nametags.construct;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class NametagUpdate {

    private String toRefresh;
    private String refreshFor;

    public NametagUpdate(Player toRefresh) {
        if(toRefresh == null) return;

        this.toRefresh = toRefresh.getName();
    }

    public NametagUpdate(Player toRefresh, Player refreshFor) {
        this.toRefresh = toRefresh.getName();
        this.refreshFor = refreshFor.getName();
    }
}