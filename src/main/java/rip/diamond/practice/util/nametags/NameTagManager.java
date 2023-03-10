package rip.diamond.practice.util.nametags;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.Tasks;
import rip.diamond.practice.util.Util;
import rip.diamond.practice.util.exception.PracticeUnexpectedException;
import rip.diamond.practice.util.nametags.construct.NameTagComparator;
import rip.diamond.practice.util.nametags.construct.NameTagInfo;
import rip.diamond.practice.util.nametags.construct.NametagUpdate;
import rip.diamond.practice.util.nametags.listener.NameTagListener;
import rip.diamond.practice.util.nametags.packet.ScoreboardTeamPacketMod;
import rip.diamond.practice.util.nametags.provider.NameTagProvider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class NameTagManager {

    private final Eden plugin;

    private final Map<String, Map<String, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    private final List<NameTagProvider> providers = new ArrayList<>();
    private final List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());

    private boolean initiated = false;
    private boolean async = true;
    private static int teamCreateIndex = 1;

    public NameTagManager(Eden plugin) {
        this.plugin = plugin;
        this.initiated = true;

        this.plugin.getServer().getPluginManager().registerEvents(new NameTagListener(), this.plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                Util.getOnlinePlayers().forEach(player -> reload(player));
            }
        }.runTaskTimerAsynchronously(plugin, 0, 2);
    }

    public void registerAdapter(NameTagProvider newProvider) {
        this.providers.add(newProvider);
        this.providers.sort(new NameTagComparator());
    }

    public void reload(Player player) {
        reloadPlayer(player);
        reloadOthersFor(player);
    }

    public void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        applyUpdate(update);
    }

    public void reloadOthersFor(Player refreshFor) {
        this.plugin.getServer().getOnlinePlayers().forEach(toRefresh -> {
            if (refreshFor != toRefresh) {
                reloadPlayer(toRefresh, refreshFor);
            }
        });
    }

    public void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        applyUpdate(update);
    }

    public void applyUpdate(NametagUpdate nametagUpdate) {
        if (async) {
            Tasks.runAsync(() -> {
                if (nametagUpdate.getToRefresh() != null){
                    Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

                    if (toRefreshPlayer == null) return;

                    if (nametagUpdate.getRefreshFor() == null) {
                        Bukkit.getOnlinePlayers().forEach(refreshFor -> reloadPlayerInternal(toRefreshPlayer, refreshFor));
                    } else {
                        Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

                        if(refreshForPlayer != null) {
                            reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
                        }
                    }
                }
            });
        } else throw new PracticeUnexpectedException("Nametag update should be run async");
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("nametag-logged-in")) return;

        NameTagInfo provided = null;

        for (NameTagProvider nametagProvider : providers) {
            provided = nametagProvider.fetchNameTag(toRefresh, refreshFor);
            if (provided != null){
                break;
            }
        }

        if (provided == null) return;

        Map<String, NameTagInfo> teamInfoMap = new HashMap<>();
        
        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }

        new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);        
    }

    public void initiatePlayer(Player player) {
        registeredTeams.forEach(teamInfo -> teamInfo.getTeamAddPacket().sendToPlayer(player));
    }

    public synchronized NameTagInfo getOrCreate(String prefix, String suffix) {
        for( NameTagInfo teamInfo : registeredTeams ) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NameTagInfo newTeam = new NameTagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        this.plugin.getServer().getOnlinePlayers().forEach(addPacket::sendToPlayer);

        return (newTeam);
    }
}