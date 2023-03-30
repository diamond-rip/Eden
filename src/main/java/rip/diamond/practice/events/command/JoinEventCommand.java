package rip.diamond.practice.events.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class JoinEventCommand extends Command {
    @CommandArgs(name = "joinevent")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();

        EdenEvent event = EdenEvent.getOnGoingEvent();

        if (event == null) {
            Language.EVENT_EVENT_IS_NOT_RUNNING.sendMessage(player);
            return;
        }
        if (event.getState() != EventState.WAITING) {
            Language.EVENT_EVENT_ALREADY_STARTED.sendMessage(player);
            return;
        }

        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.EVENT_WRONG_STATE.sendMessage(player);
            return;
        }

        Party party = Party.getByPlayer(player);
        if (party == null) {
            party = new Party(player, event.getTeamSize());
            Language.EVENT_AUTO_CREATE_PARTY_BECAUSE_NEED_A_PARTY.sendMessage(player);
        } else if (party.getAllPartyMembers().size() > event.getTeamSize()) {
            Language.EVENT_PARTY_SIZE_OVER.sendMessage(player, event.getTeamSize());
            return;
        } else if (party.getMaxSize() != event.getTeamSize()) {
            party.setMaxSize(event.getTeamSize());
            Language.EVENT_AUTO_SET_PARTY_SIZE.sendMessage(player, party.getMaxSize());
        }

        event.join(party);
        return;
    }
}
