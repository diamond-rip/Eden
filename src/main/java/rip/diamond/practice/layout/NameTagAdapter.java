package rip.diamond.practice.layout;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.nametags.construct.NameTagInfo;
import rip.diamond.practice.util.nametags.provider.NameTagProvider;

public class NameTagAdapter extends NameTagProvider {

    public NameTagAdapter() {
        super("Eden Provider", 1);
    }

    @Override
    public NameTagInfo fetchNameTag(Player target, Player viewer) {
        ChatColor prefixColor = getNameColor(target, viewer);
        return createNameTag(prefixColor.toString(), "");
    }

    private ChatColor getNameColor(Player target, Player viewer) {
        PlayerProfile profile = PlayerProfile.get(target);

        if (profile != null && (profile.getPlayerState() == PlayerState.IN_MATCH || profile.getPlayerState() == PlayerState.IN_SPECTATING) && profile.getMatch() != null) {
            Match match = profile.getMatch();
            Team team = match.getTeam(target);

            //Means it is a spectator
            if (team == null) {
                return ChatColor.GRAY;
            }

            return match.getRelationColor(viewer, target);
        }
        //Means the player is not in a match
        else {
            return ChatColor.valueOf(getPlugin().getConfigFile().getString("nametag.color"));
        }
    }
}
