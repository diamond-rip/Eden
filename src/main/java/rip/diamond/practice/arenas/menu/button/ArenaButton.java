package rip.diamond.practice.arenas.menu.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class ArenaButton extends Button {

    public final Arena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(getIcon())
                .durability(getDurability())
                .name(CC.AQUA + getName())
                .lore("", getDescription() == null ? null : CC.GRAY + getDescription(), getDescription() == null ? null : "")
                .lore(getActionDescription() == null ? null : CC.YELLOW + CC.UNDER_LINE + getActionDescription())
                .lore(getActionDescriptions())
                .build();
    }

    public Material getIcon() {
        return Material.LEVER;
    }

    public int getDurability() {
        return 0;
    }

    public abstract String getName();
    public abstract String getDescription();

    public String getActionDescription() {
        return Language.ARENA_EDIT_MENU_CLICK_TO_EDIT.toString(getName());
    }

    public List<String> getActionDescriptions() {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }
}
