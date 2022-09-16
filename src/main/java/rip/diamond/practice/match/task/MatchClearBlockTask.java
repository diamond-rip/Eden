package rip.diamond.practice.match.task;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.util.Util;

import java.util.Collection;
import java.util.function.Consumer;

@Getter
public class MatchClearBlockTask extends MatchTaskTicker {

    private final Match match;
    private final int seconds;
    private final Location location;
    private final World world;
    private final Consumer<Collection<ItemStack>> callback;

    public MatchClearBlockTask(Match match, int seconds, World world, Location location, Consumer<Collection<ItemStack>> callback) {
        super(seconds * 20, 1, false, match);
        this.match = match;
        this.seconds = seconds;
        this.world = world;
        this.location = location;
        this.callback = callback;
    }

    @Override
    public void onRun() {
        if (getTicks() <= 0) {
            cancel();

            try {
                Collection<ItemStack> itemStacks = location.clone().getBlock().getDrops();

                Util.setBlockFast(location, Material.AIR, false);
                match.getPlacedBlocks().remove(location);
                callback.accept(itemStacks);
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
