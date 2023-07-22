package rip.diamond.practice.hook.plugin.citizens;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CitizensHook {

    @Getter private final List<NPC> npcs = new ArrayList<>();

    public boolean isNPC(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid) != null;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        npcs.forEach(npc -> {
            if (npc.getEntity() instanceof Player && npc.getEntity().hasMetadata("PvP-Bot")) {
                players.add((Player) npc.getEntity());
            }
        });
        return ImmutableList.copyOf(players);
    }

    public Player getNPCPlayer(UUID uuid) {
        return (Player) CitizensAPI.getNPCRegistry().getByUniqueId(uuid).getEntity();
    }

}
