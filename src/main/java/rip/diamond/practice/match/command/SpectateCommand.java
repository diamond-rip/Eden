package rip.diamond.practice.match.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.match.menu.SpectateMenu;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class SpectateCommand extends Command {
    @CommandArgs(name = "spectate", aliases = {"spec"})
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        String[] args = command.getArgs();

        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.MATCH_SPECTATE_WRONG_STATE.sendMessage(player);
            return;
        }

        if (Party.getByPlayer(player) != null) {
            Language.MATCH_SPECTATE_HAVE_PARTY.sendMessage(player);
            return;
        }

        if (args.length == 0) {
            new SpectateMenu().openMenu(player);
            return;
        } else {
            String name = args[0];
            Player target = Bukkit.getPlayer(name);
            if (target == null || !target.isOnline()) {
                Language.MATCH_SPECTATE_NOT_ONLINE.sendMessage(player);
                return;
            }
            PlayerProfile tProfile = PlayerProfile.get(target);
            if (tProfile == null || tProfile.getMatch() == null) {
                Language.MATCH_SPECTATE_PROFILE_NOT_FOUND.sendMessage(player);
                return;
            }

            tProfile.getMatch().joinSpectate(player, target);
        }
    }
}
