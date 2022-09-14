package rip.diamond.practice.util.nametags.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.nametags.construct.NameTagInfo;

@Getter
@AllArgsConstructor
public abstract class NameTagProvider {

    private final Eden plugin = Eden.INSTANCE;

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public NameTagInfo createNameTag(String prefix, String suffix) {
        return (plugin.getNameTagManager().getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}