package rip.diamond.practice.profile.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.config.Language;
import rip.diamond.practice.leaderboard.menu.impl.KitStatsMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatsCommand extends Command {
    @CommandArgs(name = "stats", async = true)
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        String username;
        if (args.length == 0) {
            username = player.getName();
        } else {
            username = args[0];
        }

        Player target = Bukkit.getPlayer(username);
        OfflinePlayer offlineTarget = CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(username)).join();
        if (offlineTarget == null || !offlineTarget.hasPlayedBefore()) {
            Language.PROFILE_CANNOT_FIND_PLAYER.sendMessage(player);
            return;
        }

        UUID targetUUID = target == null ? offlineTarget.getUniqueId() : target.getUniqueId();
        String targetName = target == null ? offlineTarget.getName() : target.getName();

        PlayerProfile profile = PlayerProfile.get(targetUUID);
        if (profile != null) {
            new KitStatsMenu(profile).openMenu(player);
            return;
        }

        PlayerProfile finalProfile = PlayerProfile.createPlayerProfile(targetUUID, targetName);
        finalProfile.setTemporary(true);
        finalProfile.load(success -> {
            if (success) {
                new KitStatsMenu(finalProfile).openMenu(player);
                PlayerProfile.getProfiles().remove(offlineTarget.getUniqueId());
            } else {
                Language.PROFILE_ERROR_CANNOT_LOAD_PLAYER.sendMessage(player);
            }
        });
    }
}
