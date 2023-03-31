package rip.diamond.practice.arenas.menu.button.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.arenas.task.ArenaRemoveTask;
import rip.diamond.practice.util.ItemBuilder;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.menu.Button;
import rip.diamond.practice.util.serialization.LocationSerialization;

public class ArenaDetailButton extends Button {
    private final Arena arena;
    private final ArenaDetail arenaDetail;
    private final int number;
    public ArenaDetailButton(Arena arena, ArenaDetail arenaDetail, int number) {
        this.arena = arena;
        this.arenaDetail = arenaDetail;
        this.number = number;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder builder =  new ItemBuilder(Material.MAP)
                .name(Language.ARENA_DETAILS_MENU_DETAIL_NAME.toString(number))
                .lore(Language.ARENA_DETAILS_MENU_DETAIL_LORE.toStringList(player, LocationSerialization.toReadable(arenaDetail.getA()), LocationSerialization.toReadable(arenaDetail.getB()), LocationSerialization.toReadable(arenaDetail.getMin()), LocationSerialization.toReadable(arenaDetail.getMax())));
        if (arena.getArenaDetails().get(0) == arenaDetail) {
            builder.lore(Language.ARENA_DETAILS_MENU_DETAIL_LORE_NOT_DUPLICATED.toString());
        } else {
            builder.lore(Language.ARENA_DETAILS_MENU_DETAIL_LORE_CLICK_TO_DELETE.toString());
        }
        builder.lore(Language.ARENA_DETAILS_MENU_DETAIL_LORE_CLICK_TO_TELEPORT.toString());
        return builder.build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            if (arena.getArenaDetails().get(0) == arenaDetail) {
                Language.ARENA_DETAILS_MENU_DETAIL_LORE_NOT_DUPLICATED.sendMessage(player);
                return;
            }
            player.closeInventory();
            new ArenaRemoveTask(player, arena, arenaDetail);
        } else if (clickType == ClickType.RIGHT) {
            player.closeInventory();
            Util.teleport(player, arenaDetail.getA());
            Language.ARENA_DETAILS_MENU_DETAIL_TELEPORTED.sendMessage(player, arena.getName(), number);
        }
    }
}
