package rip.diamond.practice.match.task;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.util.Util;

import java.util.Collection;
import java.util.function.Consumer;

@Getter
public class MatchClearBlockTask extends MatchTaskTicker {

    private final Match match;
    private final int seconds;
    private final Location location;
    private final World world;
    private final TeamPlayer blockPlacer;
    private final Consumer<Collection<ItemStack>> callback;

    @Setter private boolean activateCallback = true;

    public MatchClearBlockTask(Match match, int seconds, World world, Location location, TeamPlayer blockPlacer, Consumer<Collection<ItemStack>> callback) {
        super(seconds * 20, 1, false, match);
        this.match = match;
        this.seconds = seconds;
        this.world = world;
        this.location = location;
        this.blockPlacer = blockPlacer;
        this.callback = callback;
    }

    @Override
    public void onRun() {
        if (getTicks() <= 0) {
            cancel();

            try {
                Collection<ItemStack> itemStacks = location.clone().getBlock().getDrops();

                if (Config.OPTIMIZATION_SET_BLOCK_FAST.toBoolean()) {
                    Util.setBlockFast(location, Material.AIR, false);
                } else {
                    location.getBlock().setType(Material.AIR);
                }
                match.getPlacedBlocks().remove(location);

                if (activateCallback) {
                    callback.accept(itemStacks);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
