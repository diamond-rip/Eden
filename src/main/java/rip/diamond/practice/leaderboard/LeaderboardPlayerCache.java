package rip.diamond.practice.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardPlayerCache {

    private String playerName;
    private UUID playerUUID;
    private int data;

}
