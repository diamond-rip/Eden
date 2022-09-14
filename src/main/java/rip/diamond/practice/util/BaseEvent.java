package rip.diamond.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.event.*;

public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}