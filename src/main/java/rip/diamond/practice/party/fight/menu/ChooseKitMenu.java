package rip.diamond.practice.party.fight.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ChooseKitMenu extends Menu {
    private final KitMatchType kitMatchType;

    @Override
    public String getTitle(Player player) {
        return Language.PARTY_CHOOSE_KIT_MENU_TITLE.toString();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().forEach(kit -> {
            if (!kit.getKitMatchTypes().contains(kitMatchType) || !kit.isEnabled()) {
                return;
            }
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kit.getDisplayIcon().clone())
                            .name(Language.PARTY_CHOOSE_KIT_MENU_BUTTON_NAME.toString(kit.getDisplayName()))
                            .lore(Language.PARTY_CHOOSE_KIT_MENU_BUTTON_LORE.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    PlayerProfile profile = PlayerProfile.get(player);
                    if (profile.getSettings().get(ProfileSettings.ARENA_SELECTION).isEnabled()) {
                        new ChooseArenaMenu(kitMatchType, kit).openMenu(player);
                        return;
                    }
                    player.closeInventory();
                    plugin.getPartyFightManager().startPartyEvent(player, kitMatchType, kit, Arena.getEnabledArena(kit));
                }
            });
        });

        return buttons;
    }
}
