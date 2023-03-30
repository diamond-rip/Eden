package rip.diamond.practice.party.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.fight.menu.OtherPartiesMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class OtherPartiesCommand extends Command {
    @CommandArgs(name = "otherparties")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            return;
        }

        Party party = Party.getByPlayer(player);
        if (party == null) {
            Language.PARTY_NOT_IN_A_PARTY.sendMessage(player);
            return;
        }
        if (!party.getLeader().getPlayer().getUniqueId().equals(player.getUniqueId())) {
            Language.PARTY_ONLY_LEADER.sendMessage(player);
            return;
        }

        new OtherPartiesMenu().openMenu(player);
    }
}
