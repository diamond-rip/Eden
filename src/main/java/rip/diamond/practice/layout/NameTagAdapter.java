package rip.diamond.practice.layout;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.match.team.Team;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.nametags.construct.NameTagInfo;
import rip.diamond.practice.util.nametags.provider.NameTagProvider;

public class NameTagAdapter extends NameTagProvider {

    public NameTagAdapter() {
        super("Eden Provider", 1);
    }

    @Override
    public NameTagInfo fetchNameTag(Player target, Player viewer) {
        String prefix = Language.translate(getPrefix(target, viewer), target);
        int length = prefix.length();
        if (length > 16) {
            Common.log(CC.RED + "[Eden] Nametag prefix should only contain 16 character. Currently prefix has " + length + " character. (" + prefix + ")");
            return createNameTag("", "");
        }
        return createNameTag(prefix, "");
    }

    private String getPrefix(Player target, Player viewer) {
        PlayerProfile profile = PlayerProfile.get(target);

        if (profile != null && (profile.getPlayerState() == PlayerState.IN_MATCH || profile.getPlayerState() == PlayerState.IN_SPECTATING) && profile.getMatch() != null) {
            Match match = profile.getMatch();
            Team team = match.getTeam(target);

            //Means it is a spectator
            if (team == null) {
                return Config.NAMETAG_PREFIX_SPECTATOR.toString();
            }

            return match.getRelationColor(viewer, target);
        }
        //Means the player is not in a match
        else {
            return Config.NAMETAG_PREFIX_LOBBY.toString();
        }
    }
}
