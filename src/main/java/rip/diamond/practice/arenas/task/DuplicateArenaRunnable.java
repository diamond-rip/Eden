package rip.diamond.practice.arenas.task;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.util.cuboid.Cuboid;
import rip.diamond.practice.util.cuboid.CuboidDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 11/25/2017
 * @author Zonix
 */

@Getter
public abstract class DuplicateArenaRunnable extends BukkitRunnable {

    private final Eden plugin = Eden.INSTANCE;
    private final Arena copiedArena;
    private int offsetX;
    private int offsetZ;
    private final int incrementX;
    private final int incrementZ;
    private Map<Location, Block> paste;

    public DuplicateArenaRunnable(Arena copiedArena, int offsetX, int offsetZ, int incrementX, int incrementZ) {
        this.copiedArena = copiedArena;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.incrementX = incrementX;
        this.incrementZ = incrementZ;
    }

    @Override
    public void run() {
        if (this.paste == null) {
            Map<Location, Block> copy = this.blocksFromTwoPoints(copiedArena.getArenaDetails().get(0).getCuboid().outset(CuboidDirection.BOTH, 20));
            this.paste = new HashMap<>();
            for (Location loc : copy.keySet()) {
                if (copy.get(loc).getType() != Material.AIR) {
                    this.paste.put(loc.clone().add(this.offsetX, 0, this.offsetZ), copy.get(loc));
                }
            }
            copy.clear();
        } else {
            Map<Location, Block> newPaste = new HashMap<>();
            for (Location loc : this.paste.keySet()) {
                if (this.paste.get(loc).getType() != Material.AIR) {
                    newPaste.put(loc.clone().add(this.incrementX, 0, this.incrementZ), this.paste.get(loc));
                }
            }
            this.paste.clear();
            this.paste.putAll(newPaste);
        }

        boolean safe = true;
        for (Location loc : this.paste.keySet()) {
            Block block = loc.getBlock();
            if (block.getType() != Material.AIR) {
                safe = false;
                break;
            }
        }

        if (!safe) {
            this.offsetX += this.incrementX;
            this.offsetZ += this.incrementZ;
            this.run();
            return;
        }

        new WorldEditRunnable(copiedArena.getA().getWorld(), paste) {
            @Override
            public void finish() {
                onComplete();
            }
        }.runTaskTimer(this.plugin, 0L, 5L);
    }

    public Map<Location, Block> blocksFromTwoPoints(Cuboid cuboid) {
        Location loc1 = cuboid.getMinimumPoint();
        Location loc2 = cuboid.getMaximumPoint();
        Map<Location, Block> blocks = new HashMap<>();

        int topBlockX = (Math.max(loc1.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc1.getBlockX(), loc2.getBlockX()));

        int topBlockY = (Math.max(loc1.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc1.getBlockY(), loc2.getBlockY()));

        int topBlockZ = (Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc1.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.put(new Location(loc1.getWorld(), x, y, z), block);
                    }
                }
            }
        }

        return blocks;
    }

    public abstract void onComplete();
}
