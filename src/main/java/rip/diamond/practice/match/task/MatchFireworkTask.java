package rip.diamond.practice.match.task;

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
import rip.diamond.practice.Eden;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.util.Common;

import java.util.Random;

public class MatchFireworkTask extends MatchTaskTicker {
    private final Color color;
    private final Match match;

    public MatchFireworkTask(Color color, Match match) {
        super(0, 20, true, match);
        this.color = color;
        this.match = match;
    }

    @Override
    public void onRun() {
        if (getTicks() <= 0) {
            cancel();
            return;
        }
        ItemStack stackFirework = new ItemStack(Material.FIREWORK);
        FireworkMeta fireworkMeta = (FireworkMeta) stackFirework.getItemMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(color).withFade(Color.WHITE).build());
        fireworkMeta.setPower(2);
        stackFirework.setItemMeta(fireworkMeta);

        int randomX = new Random().nextInt(40) - 20;
        int randomZ = new Random().nextInt(40) - 20;

        EntityFireworks firework = new EntityFireworks(((CraftWorld) match.getArenaDetail().getCuboid().getWorld()).getHandle(), match.getArenaDetail().getCuboid().getCenter().getX() + randomX, match.getArenaDetail().getCuboid().getCenter().getY(), match.getArenaDetail().getCuboid().getCenter().getZ() + randomZ, CraftItemStack.asNMSCopy(stackFirework));
        for (Player player : match.getPlayersAndSpectators()) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntity(firework, 76));
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(Eden.INSTANCE, () -> {
            firework.expectedLifespan = 0;
            for (Player player : match.getPlayersAndSpectators()) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(firework.getId(), firework.getDataWatcher(), true));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(firework, (byte) 17));
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(firework.getId()));
            }
        }, 2L);
    }

    @Override
    public void preRun() {

    }

    @Override
    public TickType getTickType() {
        return TickType.COUNT_DOWN;
    }

    @Override
    public int getStartTick() {
        return match.getKit().getGameRules().getNewRoundTime();
    }
}
