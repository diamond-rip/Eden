package rip.diamond.practice.duel.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.Eden;
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
import java.util.UUID;

@RequiredArgsConstructor
public class ChooseKitMenu extends Menu {
    private final UUID targetUUID;
    private final boolean party;

    @Override
    public String getTitle(Player player) {
        return Language.DUEL_CHOOSE_KIT_MENU_NAME.toString();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        Kit.getKits().forEach(kit -> {
            if (!kit.isEnabled()) {
                return;
            }
            if (party && !kit.getKitMatchTypes().contains(KitMatchType.SPLIT)) {
                return;
            }

            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kit.getDisplayIcon().clone())
                            .name(Language.DUEL_CHOOSE_KIT_MENU_BUTTON_NAME.toString(kit.getDisplayName()))
                            .lore(Language.DUEL_CHOOSE_KIT_MENU_BUTTON_LORE.toStringList(player))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    PlayerProfile profile = PlayerProfile.get(player);
                    if (profile.getSettings().get(ProfileSettings.ARENA_SELECTION).isEnabled()) {
                        new ChooseArenaMenu(targetUUID, kit).openMenu(player);
                        return;
                    }
                    player.closeInventory();
                    Eden.INSTANCE.getDuelRequestManager().sendDuelRequest(player, Bukkit.getPlayer(targetUUID), kit, Arena.getEnabledArena(kit));
                }
            });
        });

        return buttons;
    }
}
