package rip.diamond.practice.leaderboard;

import lombok.Getter;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.leaderboard.impl.KitLeaderboard;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LeaderboardManager {
    private final Map<Kit, KitLeaderboard> winsLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> eloLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> winstreakLeaderboard = new HashMap<>();
    private final Map<Kit, KitLeaderboard> bestWinstreakLeaderboard = new HashMap<>();

    public void init() {
        if (!Config.MONGO_ENABLED.toBoolean()) {
            return;
        }
        for (Kit kit : Kit.getKits()) {
            winsLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.WINS, kit));
            eloLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.ELO, kit));
            winstreakLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.WINSTREAK, kit));
            bestWinstreakLeaderboard.put(kit, new KitLeaderboard(LeaderboardType.BEST_WINSTREAK, kit));
        }

        Tasks.runAsyncTimer(this::update, 0L, 20L * 60L * 60L); //Updates each 60 minutes
    }

    public void update() {
        long previous = System.currentTimeMillis();
        Common.debug("正在更新排行榜... 這可能需要一段時間");
        for (Map<Kit, KitLeaderboard> datas : Arrays.asList(winsLeaderboard, eloLeaderboard, winstreakLeaderboard, bestWinstreakLeaderboard)) {
            //每五分鐘更新排行榜
            datas.values().forEach(Leaderboard::update);
        }
        long current = System.currentTimeMillis();
        Common.debug("排行榜更新完畢! 耗時" + (current - previous) + "ms");
    }

}
