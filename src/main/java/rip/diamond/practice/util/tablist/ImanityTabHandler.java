/*
 * MIT License
 *
 * Copyright (c) 2021 Imanity
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rip.diamond.practice.util.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rip.diamond.practice.Eden;
import rip.diamond.practice.config.Config;
import rip.diamond.practice.util.Checker;
import rip.diamond.practice.util.Common;
import rip.diamond.practice.util.tablist.util.IImanityTabImpl;
import rip.diamond.practice.util.tablist.util.impl.ProtocolLibTabImpl;
import rip.diamond.practice.util.tablist.util.packet.WrapperPlayServerLogin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class ImanityTabHandler {

    private static final Map<UUID, ImanityTablist> tablists = new HashMap<>();

    private final ImanityTabAdapter adapter;
    private ScheduledExecutorService thread;
    private IImanityTabImpl implementation;

    private PacketAdapter protocolListener;

    //Tablist Ticks
    private final long ticks;

    public ImanityTabHandler(ImanityTabAdapter adapter) {
        this.adapter = adapter;

        this.ticks = Config.FANCY_TABLIST_UPDATE_TICKS.toInteger();

        this.registerImplementation();
        this.setup();
    }

    private void registerImplementation() {
        if (Checker.isPluginEnabled("ProtocolLib")) {
            this.implementation = new ProtocolLibTabImpl();
        } else {
            Common.log("Cannot enable tablist implementation because ProtocolLib isn't enabled!");
        }
    }

    public void registerPlayerTablist(Player player) {
        ImanityTablist tablist = new ImanityTablist(player);
        tablists.put(player.getUniqueId(), tablist);
    }

    public void removePlayerTablist(Player player) {
        tablists.remove(player.getUniqueId());
    }

    private void setup() {
        //Ensure that the thread has stopped running
        if (this.thread != null) {
            this.thread.shutdown();
            this.thread = null;
        }

        // To ensure client will display 60 slots on 1.7
        if (Bukkit.getMaxPlayers() < 60) {
            protocolListener = new PacketAdapter(Eden.INSTANCE, PacketType.Play.Server.LOGIN) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrapperPlayServerLogin packet = new WrapperPlayServerLogin(event.getPacket());
                    packet.setMaxPlayers(60);
                    event.setPacket(packet.getHandle());
                }
            };
            ProtocolLibrary.getProtocolManager().addPacketListener(protocolListener);
        }

        Eden.INSTANCE.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                registerPlayerTablist(event.getPlayer());
            }
            @EventHandler
            public void onLeave(PlayerQuitEvent event) {
                removePlayerTablist(event.getPlayer());
            }
        }, Eden.INSTANCE);

        //Start Thread
        this.thread = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setNameFormat("Imanity-Tablist-Thread")
                .setDaemon(true)
                .setUncaughtExceptionHandler((thread1, throwable) -> throwable.printStackTrace())
            .build());

        this.thread.scheduleAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ImanityTablist tablist = tablists.get(player.getUniqueId());

                if (tablist != null) {
                    try {
                        tablist.update();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }, ticks * 50L, ticks * 50L, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (this.thread != null) {
            this.thread.shutdown();
            this.thread = null;
        }
        if (this.protocolListener != null) {
            ProtocolLibrary.getProtocolManager().removePacketListener(protocolListener);
        }
    }
}
