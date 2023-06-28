package rip.diamond.practice.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

// TODO: 7/5/2023 Replace this to paperspigot title api
public class TitleSender {

    public static void sendTitle(Player player, String text, PacketPlayOutTitle.EnumTitleAction titleAction, int fadeInTime, int showTime, int fadeOutTime) {
        if (Util.isNull(text)) {
            return;
        }
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(text) + "\",color:" + ChatColor.GOLD.name().toLowerCase() + "}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(titleAction, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInTime, showTime, fadeOutTime);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    public static void sendActionBar(Player p, String text) {
        if (Util.isNull(text)) {
            return;
        }
        CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + CC.translate(text) + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }

}
