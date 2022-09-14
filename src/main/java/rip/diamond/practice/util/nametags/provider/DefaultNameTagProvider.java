package rip.diamond.practice.util.nametags.provider;

import org.bukkit.entity.Player;
import rip.diamond.practice.util.nametags.construct.NameTagInfo;

public class DefaultNameTagProvider extends NameTagProvider {

    public DefaultNameTagProvider() {
        super("Default Provider", 0);
    }

    @Override
    public NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor) {
        return (createNameTag(toRefresh.getDisplayName().replace(toRefresh.getName(),""), ""));
    }

}
