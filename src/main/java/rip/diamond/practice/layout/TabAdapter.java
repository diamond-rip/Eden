package rip.diamond.practice.layout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.tablist.ImanityTabAdapter;
import rip.diamond.practice.util.tablist.util.BufferedTabObject;
import rip.diamond.practice.util.tablist.util.Skin;
import rip.diamond.practice.util.tablist.util.TabColumn;
import rip.diamond.practice.util.tablist.util.TablistUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabAdapter implements ImanityTabAdapter {

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        Set<BufferedTabObject> objects = new HashSet<>();

        int i = 0;
        int maxSlots = TablistUtil.getPossibleSlots(player);
        List<Player> playerList = new ArrayList<Player>(Bukkit.getOnlinePlayers()).subList(0, Math.min(Bukkit.getOnlinePlayers().size(), maxSlots));

        for (Player target : playerList) {
            int x = i / (maxSlots / 20) + 1; //Somehow ImanityTablist slot count starts at 1, so we have to start at 1 :shrug:
            int y = i % (maxSlots / 20);

            objects.add(new BufferedTabObject()
                    .slot(x)
                    .column(TabColumn.getFromOrdinal(y))
                    .text(CC.translate(Language.translate(Config.FANCY_TABLIST_FORMAT.toString().replace("{player-name}", target.getName()), target)))
                    .ping(target.spigot().getPing())
                    .skin(Skin.fromPlayer(target))
            );

            i++;
        }

        return objects;
    }

    @Override
    public String getHeader(Player player) {
        if (Language.TABLIST_HEADER.toString().equals(Language.TABLIST_HEADER.getPath())) {
            return null;
        }
        return Language.TABLIST_HEADER.toString(player);
    }

    @Override
    public String getFooter(Player player) {
        if (Language.TABLIST_FOOTER.toString().equals(Language.TABLIST_FOOTER.getPath())) {
            return null;
        }
        return Language.TABLIST_FOOTER.toString(player);
    }
}
