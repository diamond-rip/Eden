package rip.diamond.practice.match;

import org.bukkit.Material;
import org.bukkit.block.Block;
import rip.diamond.practice.Eden;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitGameRules;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.match.team.TeamPlayer;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.Util;

import java.util.Comparator;
import java.util.Objects;

public class MatchMovementHandler {

    public MatchMovementHandler() {
        Eden.INSTANCE.getSpigotAPI().getMovementHandler().injectLocationUpdate((player, from, to) -> {
            PlayerProfile profile = PlayerProfile.get(player);

            Block block = to.getBlock();
            Block underBlock = to.clone().add(0, -1, 0).getBlock();

            if (profile.getPlayerState() == PlayerState.IN_MATCH && profile.getMatch() != null) {
                Match match = profile.getMatch();
                Arena arena = match.getArenaDetail().getArena();
                Kit kit = match.getKit();
                KitGameRules gameRules = kit.getGameRules();

                if (gameRules.isStartFreeze() && match.getState() == MatchState.STARTING && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                    Util.teleport(player, from);
                    return;
                }

                if (arena.getYLimit() > player.getLocation().getY()) {
                    Util.damage(player, 99999);
                    return;
                }

                //Prevent any duplicate scoring
                //If two people go into the portal at the same time in bridge, it will count as +2 points
                //If player go into the water and PlayerMoveEvent is too slow to perform teleportation, it will run MatchNewRoundTask multiple times
                if (match.getMatchPlayers().stream().filter(Objects::nonNull).noneMatch(p -> PlayerProfile.get(p).getCooldowns().containsKey("score"))) {
                    //檢查 KitGameRules 水上即死
                    if (gameRules.isDeathOnWater() && match.getState() == MatchState.FIGHTING && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER)) {
                        if (gameRules.isPoint(match)) {
                            TeamPlayer lastHitDamager = match.getTeamPlayer(player).getLastHitDamager();
                            //玩家有機會在不被敵方攻擊的情況下死亡, 例如岩漿, 如果是這樣, 就在敵方隊伍隨便抽一個玩家出來
                            if (lastHitDamager == null) {
                                lastHitDamager = match.getOpponentTeam(match.getTeam(player)).getAliveTeamPlayers().get(0);
                            }
                            match.score(profile, lastHitDamager);
                        } else {
                            Util.damage(player, 99999);
                        }
                        return;
                    }

                    //檢查 KitGameRules 進入目標
                    if (match.getState() == MatchState.FIGHTING && underBlock.getType() == Material.ENDER_PORTAL) {
                        if (gameRules.isPortalGoal()) {
                            Team playerTeam = match.getTeam(player);
                            Team portalBelongsTo = match.getTeams().stream().min(Comparator.comparing(team -> team.getSpawnLocation().distance(to))).orElse(null);
                            if (portalBelongsTo == null) {
                                Common.log("An error occurred while finding portalBelongsTo, please contact GoodestEnglish to fix");
                                return;
                            }
                            if (portalBelongsTo != playerTeam) {
                                match.score(profile, match.getTeamPlayer(player));
                            } else {
                                //Prevent player scoring their own goal
                                Util.damage(player, 99999);
                            }
                        }
                        return;
                    }
                }
            }
        });
    }
}
