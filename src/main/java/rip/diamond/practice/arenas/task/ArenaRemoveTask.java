package rip.diamond.practice.arenas.task;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.arenas.ArenaDetail;
import rip.diamond.practice.util.TaskTicker;

public class ArenaRemoveTask extends TaskTicker {
    private final Player player;
    private final Arena arena;
    private final ArenaDetail arenaDetail;

    public ArenaRemoveTask(Player player, Arena arena, ArenaDetail arenaDetail) {
        super(0, 1, true);
        this.player = player;
        this.arena = arena;
        this.arenaDetail = arenaDetail;
    }

    @Override
    public void onRun() {
        cancel();

        TaskManager.IMP.async(() -> {
            EditSession editSession = new EditSessionBuilder(arenaDetail.getA().getWorld().getName()).fastmode(true).allowedRegionsEverywhere().autoQueue(false).limitUnlimited().build();
            CuboidRegion copyRegion = new CuboidRegion(new Vector(arenaDetail.getMax().getX(), arenaDetail.getMax().getY(), arenaDetail.getMax().getZ()), new Vector(arenaDetail.getMin().getX(), arenaDetail.getMin().getY(), arenaDetail.getMin().getZ()));

            editSession.setBlocks(copyRegion, new BaseBlock(BlockID.AIR));
            editSession.flushQueue();
        });

        arena.getArenaDetails().remove(arenaDetail);

        if (arena.getArenaDetails().isEmpty()) {
            Arena.getArenas().remove(arena);
            Language.ARENA_REMOVE_SUCCESS_MAIN.sendMessage(player);
            arena.autoSave();
        } else {
            Language.ARENA_REMOVE_SUCCESS_DUPLICATE.sendMessage(player, arena.getName());
        }
    }

    @Override
    public void preRun() {

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
