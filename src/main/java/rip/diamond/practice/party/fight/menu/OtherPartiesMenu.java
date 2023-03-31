package rip.diamond.practice.party.fight.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.util.*;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.pagination.PaginatedMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OtherPartiesMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return Language.PARTY_OTHER_PARTIES_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        for (Party party : Party.getParties().values()) {
            if (party.getAllPartyMembers().stream().anyMatch(partyMember -> partyMember.getUniqueID().equals(player.getUniqueId()))) {
                continue;
            }
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(Material.SKULL_ITEM)
                            .durability(3)
                            .headTexture(HeadUtil.getValue(party.getLeader().getPlayer()))
                            .amount(party.getAllPartyMembers().size())
                            .name(Language.PARTY_OTHER_PARTIES_MENU_BUTTON_NAME.toString(party.getLeader().getUsername()))
                            .lore(Language.PARTY_OTHER_PARTIES_MENU_BUTTON_LORE_START.toStringList(player, party.getLeader().getUsername(), party.getAllPartyMembers().size(), party.getMaxSize()))
                            .lore(party.getPartyMembers().stream().map(pm -> CC.GRAY + " " + Symbols.BULLET + " " + CC.WHITE + pm.getUsername()).collect(Collectors.toList()))
                            .lore(Language.PARTY_OTHER_PARTIES_MENU_BUTTON_LORE_END.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.closeInventory();
                    Util.performCommand(player, "duel " + party.getLeader().getUsername());
                }
            });
        }

        return buttons;
    }
}
