package rip.diamond.practice.profile.procedure;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.diamond.practice.config.Language;

public class ProcedureListener implements Listener{

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Procedure procedure = Procedure.getProcedures().get(player.getUniqueId());
        if (procedure == null) {
            return;
        }

        event.setCancelled(true);

        String message = event.getMessage();
        if (message.equalsIgnoreCase("cancel")) {
            procedure.remove();
            Language.PROFILE_PROCEDURE_SUCCESS_CANCEL.sendMessage(player);
            return;
        }
        if (procedure.getProcedureType() == ProcedureType.CHAT) {
            procedure.call(event.getMessage());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Procedure procedure = Procedure.getProcedures().get(player.getUniqueId());
        if (procedure == null) {
            return;
        }

        event.setCancelled(true);

        if (procedure.getProcedureType() == ProcedureType.BREAK_BLOCK) {
            procedure.call(event.getClickedBlock());
        }
    }

}
