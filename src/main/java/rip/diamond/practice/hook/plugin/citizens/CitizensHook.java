package rip.diamond.practice.hook.plugin.citizens;

import com.google.common.collect.ImmutableList;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CitizensHook {

    public boolean isNPC(UUID uuid) {
        return CitizensAPI.getNPCRegistry().getByUniqueId(uuid) != null;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        CitizensAPI.getNPCRegistry().forEach(npc -> {
            if (npc.getEntity() instanceof Player) {
                players.add((Player) npc.getEntity());
            }
        });
        return ImmutableList.copyOf(players);
    }

    public Player getNPCPlayer(UUID uuid) {
        return (Player) CitizensAPI.getNPCRegistry().getByUniqueId(uuid).getEntity();
    }

}
