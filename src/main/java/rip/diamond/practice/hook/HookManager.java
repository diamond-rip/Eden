package rip.diamond.practice.hook;

import lombok.Getter;
import lombok.Setter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.Language;
import rip.diamond.practice.hook.knockback.KnockbackController;
import rip.diamond.practice.hook.knockback.impl.DefaultKnockback;
import rip.diamond.practice.hook.plugin.placeholderapi.EdenPlaceholderExpansion;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;

@Getter
@Setter
public class HookManager {

    private final Eden plugin;

    private KnockbackController knockbackController;

    public HookManager(Eden plugin) {
        this.plugin = plugin;

        knockbackController = findKnockbackController();

        if (Checker.isPlaceholderAPIEnabled()) {
            new EdenPlaceholderExpansion(plugin).register();
        }
    }

    private KnockbackController findKnockbackController() {
        try {
            for (Class<?> clazz : Util.getClassesInPackage(Eden.INSTANCE, "rip.diamond.practice.hook.knockback.impl")) {
                if (clazz.getSuperclass() == KnockbackController.class) {
                    KnockbackController kb = (KnockbackController) clazz.newInstance();
                    if (Checker.isClassExists(kb.getPackage())) {
                        Common.log(Language.HOOK_FOUND_CUSTOM_SPIGOT.toString(kb.getPluginName()));
                        return kb;
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        Common.log(Language.HOOK_CANNOT_FIND_CUSTOM_SPIGOT.toString());
        return new DefaultKnockback();
    }

}
