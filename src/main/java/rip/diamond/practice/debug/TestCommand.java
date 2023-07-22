package rip.diamond.practice.debug;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class TestCommand extends Command {

    @CommandArgs(name = "test", permission = "eden.command.test", async = true)
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        String[] args = command.getArgs();

        if (args[0].equalsIgnoreCase("1")) {
            ItemStack stackFirework = new ItemStack(Material.FIREWORK);
            FireworkMeta fireworkMeta = (FireworkMeta) stackFirework.getItemMeta();
            fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.AQUA).withFade(Color.WHITE).build());
            fireworkMeta.setPower(2);
            stackFirework.setItemMeta(fireworkMeta);

            EntityFireworks firework = new EntityFireworks(((CraftWorld) player.getWorld()).getHandle(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), CraftItemStack.asNMSCopy(stackFirework));

            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntity(firework, 76));

            Bukkit.getScheduler().runTaskLaterAsynchronously(Eden.INSTANCE, () -> {
                firework.expectedLifespan = 0;
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(firework.getId(), firework.getDataWatcher(), true));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(firework, (byte) 17));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(firework.getId()));
            }, 5L);
            Common.log("done");
            return;
        } else if (args[0].equalsIgnoreCase("2")) {
            Bukkit.getPlayer("GoodestEnglish").performCommand("party create");
            Bukkit.getPlayer("Fauzh").performCommand("party join GoodestEnglish");
            Bukkit.getPlayer("DragonL").performCommand("party join GoodestEnglish");
            return;
        }


    }
}
