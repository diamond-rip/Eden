package io.github.epicgo.sconey.element;

import org.bukkit.entity.Player;

/**
 *
 * Sconey Element Adapter interface
 * the adapter that will provide the player with scoreboard element
 */
public interface SconeyElementAdapter {

    /**
     * This method returns the scoreboard element used by this instance
     * @param player the player containing the provided scoreboard
     * @return the scoreboard element used by this instance
     */
    SconeyElement getElement(final Player player);
}
