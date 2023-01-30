package rip.diamond.practice;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.MatchState;
import rip.diamond.practice.match.impl.FFAMatch;
import rip.diamond.practice.match.impl.SoloMatch;
import rip.diamond.practice.match.impl.TeamMatch;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.profile.ProfileSettings;
import rip.diamond.practice.queue.Queue;
import rip.diamond.practice.queue.QueueProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.TimeUtil;
import rip.diamond.practice.util.Util;

import java.util.List;

@RequiredArgsConstructor
public class EdenPlaceholder {

    private final Eden plugin;
    public static final String SKIP_LINE = "<skip-line>";
    public static final String NEW_LINE = "<new-line>";

    public String translate(Player player, String str) {
        if (player != null) {
            PlayerProfile profile = PlayerProfile.get(player);

            if (profile == null) {
                return str;
            }

            Party party = Party.getByPlayer(player);
            QueueProfile qProfile = Queue.getPlayers().get(player.getUniqueId());
            Match match = profile.getMatch();
            EdenEvent event = EdenEvent.getOnGoingEvent();

            //Check if the string has {event-information}, otherwise it will cause infinite loop
            if (str.contains("{event-information}")) {
                str = str
                        .replace("{event-information}", event != null ? StringUtils.join(event.getLobbyScoreboard(player), NEW_LINE) : SKIP_LINE);
            }

            if (party != null) {
                str = str
                        .replace("{party-leader}", party.getLeader().getUsername())
                        .replace("{party-members}", party.getAllPartyMembers().size() + "")
                        .replace("{party-max}", party.getMaxSize() + "");
            }

            if (event != null) {
                str = str
                        .replace("{event-uncolored-name}", event.getUncoloredEventName())
                        .replace("{event-total-players}", event.getTotalPlayers().size() + "")
                        .replace("{event-max-players}", event.getMaxPlayers() + "")
                        .replace("{event-countdown}", event.getCountdown() == null ? "0.0" : event.getCountdown().getMilliSecondsLeft(false) + "");
            }

            if (profile.getPlayerState() == PlayerState.IN_QUEUE && qProfile != null) {
                str = str
                        .replace("{queue-kit}", qProfile.getKit().getDisplayName())
                        .replace("{queue-time}", TimeUtil.millisToTimer(qProfile.getPassed()))
                        .replace("{queue-ranked-min}", qProfile.getMinRange() + "")
                        .replace("{queue-ranked-max}", qProfile.getMaxRange() + "")
                        .replace("{ping-range}", profile.getSettings().get(ProfileSettings.PING_RANGE).toString());
            } else if (profile.getPlayerState() == PlayerState.IN_MATCH && match != null) {
                str = str
                        .replace("{match-kit}", match.getKit().getDisplayName())
                        .replace("{match-duration}", TimeUtil.millisToTimer(match.getElapsedDuration()))
                        .replace("{match-build-limit}", match.getArenaDetail().getArena().getBuildMax() + "")
                        .replace("{match-build-limit-difference}", Util.renderBuildLimit(player.getLocation().getBlockY(), match.getArenaDetail().getArena().getBuildMax()))
                ;


                for (int i = 0; i < match.getTeams().size(); i++) {
                    Team team = match.getTeams().get(i);
                    str = str.replace("{match-team" + (i+1) + "-logo}", team.getTeamColor().getTeamLogo())
                            .replace("{match-team" + (i+1) + "-bed-status}", team.isBedDestroyed() ? CC.RED + "✘" : CC.GREEN + "✔")
                            .replace("{match-team" + (i+1) + "-points}", Util.renderPointsAsBar(team, match.getKit().getGameRules().getMaximumPoints()))
                    ;
                }

                switch (match.getMatchType()) {
                    case SOLO:
                        TeamPlayer self = match.getTeamPlayer(player);
                        TeamPlayer opponent = match.getOpponent(self);
                        int solo_x = self.getHits();
                        int solo_y = opponent.getHits();
                        int soloDifference = solo_x - solo_y;

                        str = str
                                .replace("{match-solo-opponent}", opponent.getUsername())
                                .replace("{match-solo-winner}", match.getState() == MatchState.ENDING ? match.getWinningPlayers().get(0).getUsername() : "")
                                .replace("{match-solo-boxing-difference}", (solo_x > solo_y ? CC.GREEN : solo_x == solo_y ? CC.YELLOW : CC.RED) + "(" + (soloDifference > 0 ? "+" : "") + soloDifference + ")")
                                .replace("{match-solo-boxing-difference-without-bracket}", (solo_x > solo_y ? CC.GREEN : solo_x == solo_y ? CC.YELLOW : CC.RED)  + (soloDifference > 0 ? "+" : "") + soloDifference + ")")
                                .replace("{match-solo-boxing-difference-without-color}", (soloDifference > 0 ? "+" : "") + soloDifference)
                                .replace("{match-solo-boxing-self-hit}", solo_x + "")
                                .replace("{match-solo-boxing-opponent-hit}", solo_y + "")
                                .replace("{match-solo-boxing-combo}", self.getCombo() + "")
                                .replace("{match-solo-self-ping}", self.getPing() + "")
                                .replace("{match-solo-opponent-ping}", opponent.getPing() + "")
                        ;
                        break;
                    case SPLIT:
                        Team team = match.getTeam(player);
                        Team opponentTeam = match.getOpponentTeam(team);
                        int teams_x = team.getHits();
                        int teams_y = opponentTeam.getHits();
                        int teamsDifference = teams_x - teams_y;

                        str = str
                                .replace("{match-team-self-alive}", team.getAliveCount() + "")
                                .replace("{match-team-self-size}", team.getTeamPlayers().size() + "")
                                .replace("{match-team-opponent-alive}", opponentTeam.getAliveCount() + "")
                                .replace("{match-team-opponent-size}", opponentTeam.getTeamPlayers().size() + "")
                                .replace("{match-team-winner}", match.getState() == MatchState.ENDING ? ((TeamMatch) match).getWinningTeam().getLeader().getUsername() : "")
                                .replace("{match-team-boxing-difference}", (teams_x > teams_y ? CC.GREEN : teams_x == teams_y ? CC.YELLOW : CC.RED) + "(" + (teamsDifference > 0 ? "+" : "") + teamsDifference + ")")
                                .replace("{match-team-boxing-difference-without-bracket}", (teams_x > teams_y ? CC.GREEN : teams_x == teams_y ? CC.YELLOW : CC.RED) + (teamsDifference > 0 ? "+" : "") + teamsDifference)
                                .replace("{match-team-boxing-difference-without-color}", (teamsDifference > 0 ? "+" : "") + teamsDifference)
                                .replace("{match-team-boxing-self-hit}", teams_x + "")
                                .replace("{match-team-boxing-opponent-hit}", teams_y + "")
                                .replace("{match-team-boxing-combo}", team.getCombo() + "")
                        ;
                        break;
                    case FFA:
                        List<Team> ffaTeams = match.getTeams();
                        long aliveCount = ffaTeams.stream().filter(t -> !t.isEliminated()).count();

                        str = str
                                .replace("{match-ffa-alive}", aliveCount + "")
                                .replace("{match-ffa-player-size}", ffaTeams.size() + "")
                                .replace("{match-ffa-winner}", ((FFAMatch) match).getWinningTeam().getLeader().getUsername())
                        ;

                        break;
                    default:
                        break;
                }
            } else if (profile.getPlayerState() == PlayerState.IN_SPECTATING && match != null) {
                str = str
                        .replace("{spectate-kit}", match.getKit().getDisplayName())
                        .replace("{spectate-duration}", TimeUtil.millisToTimer(match.getElapsedDuration()))
                        .replace("{spectate-build-limit}", match.getArenaDetail().getArena().getBuildMax() + "")
                        .replace("{spectate-build-limit-difference}", Util.renderBuildLimit(player.getLocation().getBlockY(), match.getArenaDetail().getArena().getBuildMax()))
                ;

                for (int i = 0; i < match.getTeams().size(); i++) {
                    Team team = match.getTeams().get(i);
                    str = str.replace("{spectate-team" + (i+1) + "-logo}", team.getTeamColor().getTeamLogo())
                            .replace("{spectate-team" + (i+1) + "-bed-status}", team.isBedDestroyed() ? CC.RED + "✘" : CC.GREEN + "✔")
                            .replace("{spectate-team" + (i+1) + "-points}", Util.renderPointsAsBar(team, match.getKit().getGameRules().getMaximumPoints()))
                    ;
                }

                switch (match.getMatchType()) {
                    case SOLO:
                        TeamPlayer playerA = ((SoloMatch) match).getPlayerA();
                        TeamPlayer playerB = ((SoloMatch) match).getPlayerB();

                        str = str
                                .replace("{spectate-solo-player1}", playerA.getUsername())
                                .replace("{spectate-solo-player2}", playerB.getUsername())
                                .replace("{spectate-solo-winner}", match.getState() == MatchState.ENDING ? match.getWinningPlayers().get(0).getUsername() : "")
                                .replace("{spectate-solo-boxing-player1-hit}", playerA.getHits() + "")
                                .replace("{spectate-solo-boxing-player2-hit}", playerB.getHits() + "")
                                .replace("{spectate-solo-boxing-player1-combo}", playerA.getCombo() + "")
                                .replace("{spectate-solo-boxing-player2-combo}", playerB.getCombo() + "")
                                .replace("{spectate-solo-player1-ping}", playerA.getPing() + "")
                                .replace("{spectate-solo-player2-ping}", playerB.getPing() + "")
                        ;
                        break;
                    case SPLIT:
                        Team teamA = ((TeamMatch) match).getTeamA();
                        Team teamB = ((TeamMatch) match).getTeamB();

                        str = str
                                .replace("{spectate-team1-leader}", teamA.getLeader().getUsername() + "")
                                .replace("{spectate-team2-leader}", teamB.getLeader().getUsername() + "")
                                .replace("{spectate-team1-alive}", teamA.getAliveCount() + "")
                                .replace("{spectate-team2-alive}", teamB.getAliveCount() + "")
                                .replace("{spectate-team1-size}", teamA.getTeamPlayers().size() + "")
                                .replace("{spectate-team2-size}", teamB.getTeamPlayers().size() + "")
                                .replace("{spectate-team-winner}", match.getState() == MatchState.ENDING ? ((TeamMatch) match).getWinningTeam().getLeader().getUsername() : "")
                                .replace("{spectate-team1-boxing-hit}", teamA.getHits() + "")
                                .replace("{spectate-team2-boxing-hit}", teamB.getHits() + "")
                                .replace("{spectate-team1-boxing-combo}", teamA.getCombo() + "")
                                .replace("{spectate-team2-boxing-combo}", teamB.getCombo() + "")
                        ;
                        break;
                    case FFA:
                        List<Team> ffaTeams = match.getTeams();
                        long aliveCount = ffaTeams.stream().filter(t -> !t.isEliminated()).count();

                        str = str
                                .replace("{spectate-ffa-alive}", aliveCount + "")
                                .replace("{spectate-ffa-player-size}", ffaTeams.size() + "")
                                .replace("{spectate-ffa-winner}", ((FFAMatch) match).getWinningTeam().getLeader().getUsername())
                        ;
                        break;
                    default:
                        break;
                }
            }
        }

        if (str.contains(SKIP_LINE)) {
            return null;
        } else {
            return str
                    .replace("{online-players}", plugin.getCache().getPlayersSize() + "")
                    .replace("{queue-players}", plugin.getCache().getQueuePlayersSize() + "")
                    .replace("{match-players}", plugin.getCache().getMatchPlayersSize() + "")
                    ;
        }
    }

}
