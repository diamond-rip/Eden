package rip.diamond.practice.events.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.events.EdenEvent;
import rip.diamond.practice.events.EventState;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class JoinEventCommand extends Command {
    @CommandArgs(name = "joinevent")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();

        EdenEvent event = EdenEvent.getOnGoingEvent();

        if (event == null) {
            Common.sendMessage(player, "&c現時並沒有一個正在進行的活動!");
            return;
        }
        if (event.getState() != EventState.WAITING && event.getState() != EventState.STARTING) {
            Common.sendMessage(player, "&c活動已經開始!");
            return;
        }

        PlayerProfile profile = PlayerProfile.get(player);
        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Common.sendMessage(player, "&c你必須要在大廳才能加入活動!");
            return;
        }

        Party party = Party.getByPlayer(player);
        if (party == null) {
            party = new Party(player, event.getTeamSize());
            Common.sendMessage(player, "&7[&b活動&7] &e錦標賽需要擁有一個派對才能參加, 你已自動創建了一個派對");
        } else if (party.getPartyMembers().size() > event.getTeamSize()) {
            Common.sendMessage(player, "&c你的隊伍人數超出限制! 本活動最大人數為 " + event.getTeamSize());
            return;
        } else if (party.getMaxSize() != event.getTeamSize()) {
            party.setMaxSize(event.getTeamSize());
            Common.sendMessage(player, "&7[&b活動&7] &e你在一個隊伍的情況下加入了活動, 已把你的派對最大人數改為 &b&l" + event.getTeamSize());
        }

        event.join(party);
        return;
    }
}
