package io.github.epicgo.sconey;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

@RequiredArgsConstructor
public class SconeyListener implements Listener {

    private final SconeyHandler sconeyHandler;

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.sconeyHandler.addScoreboard(player);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.sconeyHandler.removeScoreboard(player);
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        this.sconeyHandler.stopThread();
    }
}
