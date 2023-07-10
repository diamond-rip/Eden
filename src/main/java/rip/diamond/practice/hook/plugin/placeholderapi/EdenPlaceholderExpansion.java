package rip.diamond.practice.hook.plugin.placeholderapi;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.leaderboard.Leaderboard;
import rip.diamond.practice.leaderboard.LeaderboardManager;
import rip.diamond.practice.leaderboard.LeaderboardPlayerCache;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.data.ProfileKitData;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueType;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public boolean persist() {
        return true;
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
        String[] args = param.split("_");
        PlayerProfile profile = PlayerProfile.get(player);

        if (param.startsWith("queue_unranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Queue.getPlayers().values().stream().filter(qProfile -> qProfile.getKit() == kit && qProfile.getQueueType() == QueueType.UNRANKED).count() + "";
        }
        if (param.startsWith("queue_ranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Queue.getPlayers().values().stream().filter(qProfile -> qProfile.getKit() == kit && qProfile.getQueueType() == QueueType.RANKED).count() + "";
        }
        if (param.startsWith("match_unranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == QueueType.UNRANKED).mapToInt(match -> match.getMatchPlayers().size()).sum() + "";
        }
        if (param.startsWith("match_ranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(match -> match.getKit() == kit && match.getQueueType() == QueueType.UNRANKED).mapToInt(match -> match.getMatchPlayers().size()).sum() + "";
        }
        //Requested in #228
        if (param.startsWith("kit_status")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return kit.isEnabled() ? Language.ENABLED.toString() : Language.DISABLED.toString();
        }
        if (param.startsWith("in_party")){
            return profile.getParty() == null ? Language.DISABLED.toString() : Language.ENABLED.toString();
        }
        if (param.startsWith("party_privacy")) {
            Party party = profile.getParty();
            if (party == null) {
                return "";
            }
            return party.getPrivacy().getReadable();
        }
        if (args[0].equalsIgnoreCase("player")) {
            int rankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedWon).sum();
            int rankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getRankedLost).sum();
            int unrankedWon = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedWon).sum();
            int unrankedLost = profile.getKitData().values().stream().mapToInt(ProfileKitData::getUnrankedLost).sum();
            int totalElo = profile.getKitData().values().stream().mapToInt(ProfileKitData::getElo).sum();

            if (param.equalsIgnoreCase("player_ranked_win")) {
                return rankedWon + "";
            }
            if (param.equalsIgnoreCase("player_ranked_loss")) {
                return rankedLost + "";
            }
            if (param.equalsIgnoreCase("player_unranked_win")) {
                return unrankedWon + "";
            }
            if (param.equalsIgnoreCase("player_unranked_loss")) {
                return unrankedLost + "";
            }
            if (param.equalsIgnoreCase("player_overall_win")) {
                return rankedWon + unrankedWon + "";
            }
            if (param.equalsIgnoreCase("player_overall_loss")) {
                return rankedLost + unrankedLost + "";
            }
            if (param.equalsIgnoreCase("player_total_elo")) {
                return totalElo + "";
            }
            if (param.equalsIgnoreCase("player_global_elo")) {
                return totalElo / Kit.getKits().stream().filter(kit -> kit.isEnabled() && kit.isRanked()).count() + "";
            }

            String kitName = args[2];
            if (param.startsWith("player_elo")) {
                return profile.getKitData().get(kitName).getElo() + "";
            }
            if (param.startsWith("player_peakElo")) {
                return profile.getKitData().get(kitName).getPeakElo() + "";
            }
            if (param.startsWith("player_unrankedWon")) {
                return profile.getKitData().get(kitName).getUnrankedWon() + "";
            }
            if (param.startsWith("player_unrankedLost")) {
                return profile.getKitData().get(kitName).getUnrankedLost() + "";
            }
            if (param.startsWith("player_rankedWon")) {
                return profile.getKitData().get(kitName).getRankedWon() + "";
            }
            if (param.startsWith("player_rankedLost")) {
                return profile.getKitData().get(kitName).getRankedLost() + "";
            }
            if (param.startsWith("player_bestWinstreak")) {
                return profile.getKitData().get(kitName).getBestWinstreak() + "";
            }
            if (param.startsWith("player_winstreak")) {
                return profile.getKitData().get(kitName).getWinstreak() + "";
            }
        }
        if (args[0].equalsIgnoreCase("leaderboard")) {
            //If database is disabled, there will be no leaderboard data. So we are going to return "Database not enabled"
            if (!Config.MONGO_ENABLED.toBoolean()) {
                return "Database isn't enabled";
            }

            String kitName = args[3];
            Kit kit = Kit.getByName(kitName);
            int position = Integer.parseInt(args[4]);
            LeaderboardManager manager = Eden.INSTANCE.getLeaderboardManager();
            if (param.startsWith("leaderboard_bestWinstreak_player_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getBestWinstreakLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getPlayerName();
            }
            if (param.startsWith("leaderboard_bestWinstreak_winstreak_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getBestWinstreakLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getData() + "";
            }
            if (param.startsWith("leaderboard_elo_player_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getEloLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getPlayerName();
            }
            if (param.startsWith("leaderboard_elo_elo_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getEloLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getData() + "";
            }
            if (param.startsWith("leaderboard_wins_player_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getWinsLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getPlayerName();
            }
            if (param.startsWith("leaderboard_wins_win_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getWinsLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getData() + "";
            }
            if (param.startsWith("leaderboard_winstreak_player_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getWinstreakLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getPlayerName();
            }
            if (param.startsWith("leaderboard_winstreak_winstreak_")) {
                LinkedHashMap<Integer, LeaderboardPlayerCache> leaderboard = manager.getWinstreakLeaderboard().get(kit).getLeaderboard();
                if (leaderboard.size() < position) {
                    return "-";
                }
                return leaderboard.get(position).getData() + "";
            }
        }

        return null; // Placeholder is unknown by the Expansion
    }

}
