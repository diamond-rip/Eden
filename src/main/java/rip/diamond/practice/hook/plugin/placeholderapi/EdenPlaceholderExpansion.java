package rip.diamond.practice.hook.plugin.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueType;

@RequiredArgsConstructor
public class EdenPlaceholderExpansion extends PlaceholderExpansion {

    private final Eden plugin;

    @Override
    public String getIdentifier() {
        return "eden";
    }

    @Override
    public String getAuthor() {
        return "GoodestEnglish";
    }

    @Override
    public String getVersion() {
        return Eden.INSTANCE.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String param) {
        if (player == null) {
            return null;
        }
        PlayerProfile profile = PlayerProfile.get(player);

        int rankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedWon).sum();
        int rankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedLost).sum();
        int unrankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedWon).sum();
        int unrankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedLost).sum();

        if (param.equalsIgnoreCase("ranked_win")) {
            return rankedWon + "";
        }
        if (param.equalsIgnoreCase("ranked_loss")) {
            return rankedLost + "";
        }
        if (param.equalsIgnoreCase("unranked_win")) {
            return unrankedWon + "";
        }
        if (param.equalsIgnoreCase("unranked_loss")) {
            return unrankedLost + "";
        }
        if (param.equalsIgnoreCase("overall_win")) {
            return rankedWon + unrankedWon + "";
        }
        if (param.equalsIgnoreCase("overall_loss")) {
            return rankedLost + unrankedLost + "";
        }
        if (param.startsWith("queue_unranked_")) {
            String kitName = param.split("_")[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Queue.getPlayers().values().stream().filter(qProfile -> qProfile.getKit() == kit && qProfile.getQueueType() == QueueType.UNRANKED).count() + "";
        }
        if (param.startsWith("queue_ranked_")) {
            String kitName = param.split("_")[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Queue.getPlayers().values().stream().filter(qProfile -> qProfile.getKit() == kit && qProfile.getQueueType() == QueueType.RANKED).count() + "";
        }
        if (param.startsWith("match_unranked_")) {
            String kitName = param.split("_")[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == QueueType.UNRANKED).mapToInt(match -> match.getMatchPlayers().size()).sum() + "";
        }
        if (param.startsWith("match_ranked_")) {
            String kitName = param.split("_")[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == QueueType.UNRANKED).mapToInt(match -> match.getMatchPlayers().size()).sum() + "";
        }

        // TODO: 28/1/2023 Add leaderboard

        return null; // Placeholder is unknown by the Expansion
    }

}
