package rip.diamond.practice.leaderboard;

import lombok.AllArgsConstructor;
import rip.diamond.practice.kits.Kit;

@AllArgsConstructor
public enum LeaderboardType {

    WINS("kitData.{kit}.won"),
    ELO("kitData.{kit}.elo"),
    WINSTREAK("kitData.{kit}.winstreak"),
    BEST_WINSTREAK("kitData.{kit}.bestWinstreak")
    ;

    private final String path;

    public String getPath(Kit kit) {
        return path.replace("{kit}", kit.getName());
    }

}
