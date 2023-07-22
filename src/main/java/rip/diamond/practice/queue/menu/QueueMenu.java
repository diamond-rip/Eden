package rip.diamond.practice.queue.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueType;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends Menu {

    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return Language.QUEUE_MENU_TITLE.toString(queueType.getReadable());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        Kit.getKits().stream()
                .filter(Kit::isEnabled)
                .filter(kit -> queueType == QueueType.UNRANKED || kit.isRanked())
                .forEach(kit -> buttons.put(buttons.size(), new Button() {
                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return new ItemBuilder(kit.getDisplayIcon().clone())
                                .name(kit.getDisplayName())
                                .lore(kit.getDescription())
                                .lore(Language.QUEUE_MENU_BUTTON_LORE.toStringList(player,
                                        Queue.getPlayers().values().stream().filter(profile -> profile.getKit() == kit && profile.getQueueType() == queueType).count(),
                                        Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == queueType).mapToInt(match -> match.getMatchPlayers().size()).sum(),
                                        kit.getDisplayName()
                                ))
                                .build();
                    }

                    @Override
                    public void clicked(Player player, ClickType clickType) {
                        player.closeInventory();
                        Queue.joinQueue(player, kit, queueType);
                    }
        }));
        return buttons;
    }

}
