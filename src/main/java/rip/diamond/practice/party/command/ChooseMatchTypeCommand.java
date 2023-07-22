package rip.diamond.practice.party.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.arenas.Arena;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.kits.Kit;
import rip.diamond.practice.kits.KitMatchType;
import rip.diamond.practice.party.Party;
import rip.diamond.practice.party.fight.menu.ChooseArenaMenu;
import rip.diamond.practice.party.fight.menu.ChooseKitMenu;
import rip.diamond.practice.party.fight.menu.ChooseMatchTypeMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class ChooseMatchTypeCommand extends Command {
    @CommandArgs(name = "choosematchtype")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);
        String[] args = command.getArgs();
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

        if (args.length == 0) {
            new ChooseMatchTypeMenu().openMenu(player);
            return;
        } else if (args.length == 1) {
            if (!Checker.isKitMatchType(args[0])) {
                Language.PARTY_INVALID_MATCH_TYPE.sendMessage(player, args[0]);
                return;
            }
            KitMatchType type = KitMatchType.valueOf(args[0]);
            new ChooseKitMenu(type).openMenu(player);
            return;
        } else if (args.length == 2) {
            if (!Checker.isKitMatchType(args[0])) {
                Language.PARTY_INVALID_MATCH_TYPE.sendMessage(player, args[0]);
                return;
            }
            KitMatchType type = KitMatchType.valueOf(args[0]);

            Kit kit = Kit.getByName(args[1]);
            if (kit == null) {
                Language.DUEL_INVALID_KIT.sendMessage(player, args[1]);
                return;
            }
            new ChooseArenaMenu(type, kit).openMenu(player);
            return;
        } else if (args.length == 3) {
            if (!Checker.isKitMatchType(args[0])) {
                Language.PARTY_INVALID_MATCH_TYPE.sendMessage(player, args[0]);
                return;
            }
            KitMatchType type = KitMatchType.valueOf(args[0]);

            Kit kit = Kit.getByName(args[1]);
            if (kit == null) {
                Language.PARTY_INVALID_KIT.sendMessage(player, args[1]);
                return;
            }

            Arena arena = Arena.getEnabledArena(args[2], kit);
            if (arena == null) {
                Language.PARTY_INVALID_ARENA.sendMessage(player, args[2]);
                return;
            }

            plugin.getPartyFightManager().startPartyEvent(player, type, kit, Arena.getEnabledArena(kit));
            return;
        }
    }
}
