package rip.diamond.practice.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;

@Getter
@RequiredArgsConstructor
public abstract class Leaderboard {

    private final LeaderboardType type;
    private final LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = new LinkedHashMap<>();

    public abstract void update();

}
