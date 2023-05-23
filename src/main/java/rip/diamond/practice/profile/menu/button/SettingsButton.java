package rip.diamond.practice.profile.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.event.SettingsChangeEvent;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.option.Option;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SettingsButton extends Button {

    private final ProfileSettings settings;

    @Override
    public ItemStack getButtonItem(Player player) {
        PlayerProfile profile = PlayerProfile.get(player);

        return new ItemBuilder(settings.getIcon())
                .name(CC.AQUA + settings.getName())
                .lore(settings.getDescription())
                .lore(settings.getOptions().stream().map(options -> (profile.getSettings().get(settings).equals(options) ? CC.GREEN + " » " : CC.GRAY + "   ") + options.getName()).collect(Collectors.toList()))
                .lore("", hasPermission(player) ? Language.PROFILE_SETTINGS_MENU_CLICK_TO_SWITCH_SETTINGS.toString() : Language.PROFILE_SETTINGS_MENU_NO_PERMISSION.toString())
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        PlayerProfile profile = PlayerProfile.get(player);
        Option currentOption = profile.getSettings().get(settings);

        if (!hasPermission(player)) {
            Language.PROFILE_SETTINGS_MENU_NO_PERMISSION.sendMessage(player);
            return;
        }
        if (clickType.isLeftClick()) {
            profile.getSettings().replace(settings, settings.getNextOption(currentOption));
            profile.getSettings().get(settings).run(player);
        } else if (clickType.isRightClick()) {
            profile.getSettings().replace(settings, settings.getLastOption(currentOption));
            profile.getSettings().get(settings).run(player);
        }

        settings.runSettingsChangeEvent(player, profile);
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }

    private boolean hasPermission(Player player) {
        return settings.getPermission() == null || player.hasPermission(settings.getPermission());
    }
}
