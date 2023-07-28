package rip.diamond.practice.profile.command;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import rip.diamond.practice.Eden;
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
        if (target != null) {
            PlayerProfile profile = PlayerProfile.get(target);
            new KitStatsMenu(profile).openMenu(player);
            return;
        }

        //If player isn't online...
        Document document = Eden.INSTANCE.getMongoManager().getProfiles().find(Filters.eq("lowerCaseUsername", username.toLowerCase())).first();
        if (document == null) {
            Language.PROFILE_CANNOT_FIND_PLAYER.sendMessage(player);
            return;
        }

        UUID targetUUID = UUID.fromString(document.getString("uuid"));
        String targetName = document.getString("username");

        PlayerProfile finalProfile = PlayerProfile.createPlayerProfile(targetUUID, targetName);
        finalProfile.setTemporary(true);
        finalProfile.load(document, success -> {
            if (success) {
                new KitStatsMenu(finalProfile).openMenu(player);
                PlayerProfile.getProfiles().remove(targetUUID);
            } else {
                Language.PROFILE_ERROR_CANNOT_LOAD_PLAYER.sendMessage(player);
            }
        });
    }
}
