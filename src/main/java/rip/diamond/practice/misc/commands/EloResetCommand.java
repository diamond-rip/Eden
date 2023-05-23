package rip.diamond.practice.misc.commands;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.misc.task.EloResetTask;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.util.CC;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EloResetCommand extends Command {

    @CommandArgs(name = "eloreset", permission = "eden.command.eloreset", inGameOnly = false)
    public void execute(CommandArguments command) {
        CommandSender sender = command.getSender();

        if (Bukkit.getOnlinePlayers().size() != 0) {
            Common.sendMessage(sender, CC.RED + "You cannot use this command when there is online players.");
            return;
        }

        if (PlayerProfile.getProfiles().size() != 0) {
            Common.sendMessage(sender, CC.RED + "You cannot use this command when player profile is not equal to 0");
            return;
        }

        if (!Config.MONGO_ENABLED.toBoolean()) {
            Common.sendMessage(sender, CC.RED + "You cannot use this command when mongo database is not enabled");
            return;
        }

        String[] args = command.getArgs();

        if (args.length == 0) {
            List<Document> documents = Eden.INSTANCE.getMongoManager().getProfiles().find().into(new ArrayList<>());

            new EloResetTask(documents);
        } else if (args.length == 1) {
            UUID uuid = UUID.fromString(args[0]);
            Document document = Eden.INSTANCE.getMongoManager().getProfiles().find(Filters.eq("uuid", uuid.toString())).first();
            if (document == null) {
                Common.sendMessage(sender, CC.RED + "Cannot find a document with uuid '" + uuid.toString() + "'");
                return;
            }
            new EloResetTask(Collections.singletonList(document));
        }
    }
}
