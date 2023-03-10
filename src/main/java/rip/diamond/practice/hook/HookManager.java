package rip.diamond.practice.hook;

import lombok.Getter;
import lombok.Setter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.hook.plugin.citizens.CitizensHook;
import rip.diamond.practice.hook.plugin.placeholderapi.EdenPlaceholderExpansion;
import rip.diamond.practice.hook.spigot.ImanitySpigot3Hook;
import rip.diamond.practice.util.Checker;
import rip.diamond.spigotapi.SpigotType;

@Getter
@Setter
public class HookManager {

    private final Eden plugin;

    private ImanitySpigot3Hook imanitySpigot3Hook;
    private CitizensHook citizensHook;

    public HookManager(Eden plugin) {
        this.plugin = plugin;

        if (plugin.getSpigotAPI().getSpigotType() == SpigotType.IMANITY_SPIGOT_3) {
            this.imanitySpigot3Hook = new ImanitySpigot3Hook();
        }

        if (Checker.isPluginEnabled("PlaceholderAPI")) {
            new EdenPlaceholderExpansion(plugin).register();
        }
        if (Checker.isPluginEnabled("Citizens")) {
            this.citizensHook = new CitizensHook();
        }
    }

}
