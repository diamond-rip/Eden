package rip.diamond.practice.hook.plugin.citizens;

import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import rip.diamond.practice.Eden;

@RequiredArgsConstructor
public class CitizensListener implements Listener {

    private final Eden plugin;
    private final CitizensHook hook;

    @EventHandler
    public void onNPCCreate(NPCCreateEvent event) {
        NPC npc = event.getNPC();
        hook.getNpcs().add(npc);
    }

    @EventHandler
    public void onNPCRemove(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
        hook.getNpcs().remove(npc);
    }

}
