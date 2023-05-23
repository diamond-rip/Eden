package rip.diamond.practice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.KitLoadout;
import rip.diamond.practice.util.BasicConfigFile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.ItemBuilder;

public class EdenItems {

    public static EdenItem LOBBY_UNRANKED_QUEUE = loadItem("items.lobby.unranked-queue");
    public static EdenItem LOBBY_RANKED_QUEUE = loadItem("items.lobby.ranked-queue");
    public static EdenItem LOBBY_CREATE_EVENT = loadItem("items.lobby.create-event");
    public static EdenItem LOBBY_JOIN_EVENT = loadItem("items.lobby.join-event");
    public static EdenItem LOBBY_PARTY_OPEN = loadItem("items.lobby.party-open");
    public static EdenItem LOBBY_LEADERBOARD = loadItem("items.lobby.leaderboard");
    public static EdenItem LOBBY_SETTINGS = loadItem("items.lobby.settings");
    public static EdenItem LOBBY_EDITOR = loadItem("items.lobby.editor");
    public static EdenItem PARTY_PARTY_LIST = loadItem("items.party.party-list");
    public static EdenItem PARTY_PARTY_FIGHT = loadItem("items.party.party-fight");
    public static EdenItem PARTY_OTHER_PARTIES = loadItem("items.party.other-parties");
    public static EdenItem PARTY_EDITOR = loadItem("items.party.editor");
    public static EdenItem PARTY_PARTY_LEAVE = loadItem("items.party.party-leave");
    public static EdenItem QUEUE_LEAVE_QUEUE = loadItem("items.queue.leave-queue");
    public static EdenItem MATCH_REQUEUE = loadItem("items.match.requeue");
    public static EdenItem SPECTATE_LEAVE_SPECTATE = loadItem("items.spectate.leave-spectate");
    public static EdenItem SPECTATE_TELEPORTER = loadItem("items.spectate.teleporter");
    public static EdenItem SPECTATE_TOGGLE_VISIBILITY_OFF = loadItem("items.spectate.toggle-visibility-off");
    public static EdenItem SPECTATE_TOGGLE_VISIBILITY_ON = loadItem("items.spectate.toggle-visibility-on");
    public static EdenItem INVALID = new EdenItem(true, new ItemBuilder(Material.BARRIER).name(CC.RED + "Invalid data!").build(), 0, "");

    public static void giveItem(Player player, EdenItem item) {
        if (!item.isEnabled()) {
            return;
        }

        ItemStack itemStack = item.getItemStack();
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        compound.set("command", new NBTTagString(item.getCommand()));
        itemStack = CraftItemStack.asBukkitCopy(nmsItem);

        if (item.getSlot() == -1) {
            player.setItemInHand(itemStack);
            return;
        }
        player.getInventory().setItem(item.getSlot(), itemStack);
    }

    private static EdenItem loadItem(String path) {
        BasicConfigFile file = Eden.INSTANCE.getItemFile();
        try {
            ItemStack itemStack =  new ItemBuilder(Material.valueOf(file.getString(path + ".material")))
                    .durability(file.getInt(path + ".durability"))
                    .name(file.getString(path + ".name"))
                    .lore(file.getStringList(path + ".lore"))
                    .build();
            return new EdenItem(file.getBoolean(path + ".enabled"), itemStack, file.getInt(path + ".slot"), file.getString(path + ".command"));
        } catch (Exception e) {
            return INVALID;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class EdenItem {
        private final boolean enabled;
        private final ItemStack itemStack;
        private final int slot;
        private final String command;
    }
}
