package rip.diamond.practice.match.task;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchTaskTicker;
import rip.diamond.practice.util.Util;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MatchClearBlockTask extends MatchTaskTicker {

    private final Match match;
    private final int seconds;
    private final List<Location> locations;
    private final World world;

    public MatchClearBlockTask(Match match, int seconds, World world, List<Location> locations) {
        super(seconds * 20, 1, false, match);
        this.match = match;
        this.seconds = seconds;
        this.world = world;
        this.locations = new ArrayList<>(locations);
    }

    @Override
    public void onRun() {
        if (getTicks() <= 0) {
            cancel();

            for (Location location : locations) {
                try {
                    Util.setBlockFast(location, Material.AIR, false);
                    match.getPlacedBlocks().remove(location);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
