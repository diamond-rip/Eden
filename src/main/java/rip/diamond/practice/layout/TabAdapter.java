package rip.diamond.practice.layout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.HeadUtil;
import rip.diamond.practice.util.tablist.client.ClientVersionUtil;
import rip.diamond.practice.util.tablist.entry.TabElement;
import rip.diamond.practice.util.tablist.entry.TabElementHandler;
import rip.diamond.practice.util.tablist.entry.TabEntry;

public class TabAdapter implements TabElementHandler {

    /**
     * Get the tab element of a player
     *
     * @param player the player
     * @return the element
     */
    @Override
    public TabElement getElement(Player player) {
        int total = ClientVersionUtil.getProtocolVersion(player) == 47 ? 80 : 60;
        final TabElement element = new TabElement();

        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            int x = i % (total / 20);
            int y = i / (total / 20);

            element.add(new TabEntry(x, y, p.getName(), p.spigot().getPing(), HeadUtil.getValues(p)));

            i++;
        }

        return element;
    }

}
