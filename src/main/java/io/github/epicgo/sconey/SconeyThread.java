package io.github.epicgo.sconey;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.diamond.practice.util.Util;

public class SconeyThread extends Thread {

    private final SconeyHandler sconeyHandler;

    public SconeyThread(final SconeyHandler sconeyHandler) {
        super("Board - Thread tick");
        this.sconeyHandler = sconeyHandler;

        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            this.tick();
            try {
                Thread.sleep(50L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tick logic for thread.
     */
    private void tick() {
        for (final Player player : Util.getOnlinePlayers()) {
            try {
                final SconeyPlayer sconeyPlayer = this.sconeyHandler.getScoreboard(player);
                if (sconeyPlayer == null) return;

                sconeyPlayer.handleUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
