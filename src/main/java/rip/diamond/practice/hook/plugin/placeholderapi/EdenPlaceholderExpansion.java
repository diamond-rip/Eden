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
        if (param.startsWith("party")) {
            Party party = profile.getParty();
            if (party == null) {
                return "";
            }
            if (param.equalsIgnoreCase("party_privacy")) {
                return party.getPrivacy().getReadable();
            }
            if (param.equalsIgnoreCase("party_leader")) {
                return party.getLeader().getUsername();
            }
        }
        if (param.startsWith("queue")) {
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
        }
        if (param.startsWith("match_unranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(m -> m.getKit() == kit && m.getQueueType() == QueueType.UNRANKED).mapToInt(m -> m.getMatchPlayers().size()).sum() + "";
        }
        if (param.startsWith("match_ranked_")) {
            String kitName = args[2];
            Kit kit = Kit.getByName(kitName);
            if (kit == null) {
                return "Unable to find kit " + kitName;
            }
            return Match.getMatches().values().stream().filter(m -> m.getKit() == kit && m.getQueueType() == QueueType.UNRANKED).mapToInt(m -> m.getMatchPlayers().size()).sum() + "";
        }

        if (param.startsWith("match")) {
            Match match = profile.getMatch();
            if (match == null) {
                return "Player isn't in a match";
            }
            //Requested in #467
            if (param.equalsIgnoreCase("match_match_type")) {
                return match.getMatchType().getReadable();
            }
            if (param.equalsIgnoreCase("match_queue_type")) {
                return match.getQueueType().getReadable();
            }
            //Requested in #445
            if (param.equalsIgnoreCase("match_player_team_color")) {
                return match.getTeam(player).getTeamColor().getColor();
            }
            if (param.equalsIgnoreCase("match_player_team_name")) {
                return match.getTeam(player).getTeamColor().getTeamName();
            }
            if (param.equalsIgnoreCase("match_player_team_logo")) {
                return match.getTeam(player).getTeamColor().getTeamLogo();
            }
            if (param.equalsIgnoreCase("match_arena_name")) {
                return match.getArenaDetail().getArena().getDisplayName();
            }
            if (param.equalsIgnoreCase("match_kit_name")) {
                return match.getKit().getDisplayName();
            }
        }
        if (args[0].equalsIgnoreCase("player")) {
            if (param.equalsIgnoreCase("player_status")) {
                return profile.getPlayerState().name();
            }

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
