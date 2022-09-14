package rip.diamond.practice.kiteditor.command;

import org.bukkit.entity.Player;
import rip.diamond.practice.Language;
import rip.diamond.practice.kiteditor.menu.KitEditorSelectKitMenu;
import rip.diamond.practice.profile.PlayerProfile;
import rip.diamond.practice.profile.PlayerState;
import rip.diamond.practice.util.command.Command;
import rip.diamond.practice.util.command.CommandArgs;
import rip.diamond.practice.util.command.argument.CommandArguments;

public class EditKitsCommand extends Command {
    @CommandArgs(name = "editkits")
    public void execute(CommandArguments command) {
        Player player = command.getPlayer();
        PlayerProfile profile = PlayerProfile.get(player);

        if (profile.getPlayerState() != PlayerState.IN_LOBBY) {
            Language.KIT_EDITOR_CANNOT_EDIT_WRONG_STATE.sendMessage(player);
            return;
        }

        new KitEditorSelectKitMenu().openMenu(player);
    }
}
