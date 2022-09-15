package rip.diamond.practice.hook;

import lombok.Getter;
import lombok.Setter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.hook.knockback.SpigotController;
import rip.diamond.practice.hook.knockback.impl.DefaultSpigot;
import rip.diamond.practice.hook.plugin.placeholderapi.EdenPlaceholderExpansion;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;

@Getter
@Setter
public class HookManager {

    private final Eden plugin;

    private SpigotController spigotController;

    public HookManager(Eden plugin) {
        this.plugin = plugin;

        spigotController = findKnockbackController();

        if (Checker.isPlaceholderAPIEnabled()) {
            new EdenPlaceholderExpansion(plugin).register();
        }
    }

    private SpigotController findKnockbackController() {
        try {
            for (Class<?> clazz : Util.getClassesInPackage(Eden.INSTANCE, "rip.diamond.practice.hook.knockback.impl")) {
                if (clazz.getSuperclass() == SpigotController.class) {
                    SpigotController kb = (SpigotController) clazz.newInstance();
                    if (Checker.isClassExists(kb.getPackage())) {
                        Common.log(Language.HOOK_FOUND_CUSTOM_SPIGOT.toString(kb.getPluginName()));
                        return kb;
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        Common.log(Language.HOOK_CANNOT_FIND_CUSTOM_SPIGOT.toString());
        return new DefaultSpigot();
    }

}
