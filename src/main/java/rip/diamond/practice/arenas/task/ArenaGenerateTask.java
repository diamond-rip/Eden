package rip.diamond.practice.arenas.task;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.util.*;

public class ArenaGenerateTask extends TaskTicker {
    private final Player player;
    private final Arena arena;

    public ArenaGenerateTask(Player player, Arena arena) {
        super(0, 1, false);
        this.player = player;
        this.arena = arena;
    }

    @Override
    public void onRun() {
        cancel();
        new DuplicateArenaRunnable(arena, 10000, 10000, 500, 500) {
            @Override
            public void onComplete() {
                World world = arena.getMin().getWorld();

                double minX = arena.getMin().getX() + this.getOffsetX();
                double minZ = arena.getMin().getZ() + this.getOffsetZ();
                double maxX = arena.getMax().getX() + this.getOffsetX();
                double maxZ = arena.getMax().getZ() + this.getOffsetZ();

                double aX = arena.getA().getX() + this.getOffsetX();
                double aZ = arena.getA().getZ() + this.getOffsetZ();
                double bX = arena.getB().getX() + this.getOffsetX();
                double bZ = arena.getB().getZ() + this.getOffsetZ();
                double spectatorX = arena.getSpectator().getX() + this.getOffsetX();
                double spectatorZ = arena.getSpectator().getZ() + this.getOffsetZ();

                Location min = new Location(world, minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                Location max = new Location(world, maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                Location a = new Location(world, aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
                Location b = new Location(world, bX, arena.getB().getY(), bZ, arena.getB().getYaw(), arena.getB().getPitch());
                Location spectator = new Location(world, spectatorX, arena.getSpectator().getY(), spectatorZ, arena.getSpectator().getYaw(), arena.getSpectator().getPitch());

                ArenaDetail arenaDetail = new ArenaDetail(arena, a, b, spectator, min, max);
                arena.getArenaDetails().add(arenaDetail);

                new Clickable(Language.ARENA_GENERATE_DISPLAY.toString(arena.getName(), minX, minZ), Language.ARENA_GENERATE_HOVER.toString(), "/tp " + aX + " " + arena.getA().getY() + " " + aZ + " " + arena.getA().getYaw() + " " + arena.getA().getPitch()).sendToPlayer(player);
            }
        }.run();
    }

    @Override
    public void preRun() {
        Language.ARENA_GENERATE_COPYING.sendMessage(player);
    }

    @Override
    public TickType getTickType() {
        return TickType.NONE;
    }

    @Override
    public int getStartTick() {
        return 0;
    }
}
