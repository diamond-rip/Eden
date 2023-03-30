package rip.diamond.practice.match.command;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.Match;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class NoSpeedCommand extends Command {
    @CommandArgs(name = "nospeed")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        Match match = profile.getMatch();
        if (match == null) {
            Language.MATCH_NO_SPEED_NOT_IN_MATCH.sendMessage(player);
            return;
        }
        if (!match.getKit().getGameRules().isBoxing()) {
            Language.MATCH_NO_SPEED_NOT_BOXING.sendMessage(player);
            return;
        }
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.removePotionEffect(PotionEffectType.SPEED);
            Language.MATCH_NO_SPEED_SUCCESS_REMOVED.sendMessage(player);
        } else {
            match.getKit().getEffects().forEach(player::addPotionEffect);
            Language.MATCH_NO_SPEED_SUCCESS_ADDED.sendMessage(player);
        }
    }
}
