package rip.diamond.practice.util.tablist;

import rip.diamond.practice.util.tablist.entry.TabElement;
import rip.diamond.practice.util.tablist.entry.TabEntry;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class TabAdapter {

    /**
     * Setup the profiles of the tab adapter
     */
    public TabAdapter setupProfiles(Player player) {
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 4; x++) {
                final int index = y * 4 + x;
                final String text = "§0§" + x + (y > 9
                        ? "§" + String.valueOf(y).toCharArray()[0] + "§" + String.valueOf(y).toCharArray()[1]
                        : "§0§" + String.valueOf(y).toCharArray()[0]
                );

                this.createProfiles(index, text, player);
            }
        }

        return this;
    }

    /**
     * Handle an element being send to a player
     *
     * @param player  the player
     * @param element the element to send
     */
    public TabAdapter handleElement(Player player, TabElement element) {
        final int rows = this.getMaxElements(player) / 20;

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < rows; x++) {
                final TabEntry entry = element.getEntry(x, y);
                final int index = y * rows + x;

                this.sendEntryData(player, index, entry.getPing(), entry.getText());

                if (entry.getSkinData() != null && entry.getSkinData().length > 1) {
                    this.updateSkin(entry.getSkinData(), index, player);
                }
            }
        }


        return this;
    }

    /**
     * Split the text to display on the tablist
     *
     * @param text the text to split
     * @return the split text
     */
    public String[] splitText(String text) {
        if (text.length() < 17) {
            return new String[]{text, ""};
        } else {
            final String left = text.substring(0, 16);
            final String right = text.substring(16);

            return left.endsWith("§")
                    ? new String[]{left.substring(0, left.toCharArray().length - 1), StringUtils.left(ChatColor.getLastColors(left) + "§" + right, 16)}
                    : new String[]{left, StringUtils.left(ChatColor.getLastColors(left) + right, 16)};
        }
    }

    /**
     * Setup the scoreboard for the player
     *
     * @param player the player to setup the scoreboard for
     * @param text   the text to display
     * @param name   the name of the team
     */
    public void setupScoreboard(Player player, String text, String name) {
        final String[] splitText = this.splitText(text);

        final Scoreboard scoreboard = player.getScoreboard() == null
                ? Bukkit.getScoreboardManager().getNewScoreboard()
                : player.getScoreboard();

        final Team team = scoreboard.getTeam(name) == null
                ? scoreboard.registerNewTeam(name)
                : scoreboard.getTeam(name);

        if (!team.hasEntry(name)) {
            team.addEntry(name);
        }

        team.setPrefix(splitText[0]);
        team.setSuffix(splitText[1]);

        player.setScoreboard(scoreboard);
    }

    /**
     * Update the skin on the tablist for a player
     *
     * @param skinData the data of the new skin
     * @param index    the index of the profile
     * @param player   the player to update the skin for
     */
    public abstract void updateSkin(String[] skinData, int index, Player player);

    /**
     * Check if the player should be able to see the fourth row
     *
     * @param player the player
     * @return whether they should be able to see the fourth row
     */
    public abstract int getMaxElements(Player player);

    /**
     * Create a new game profile
     *
     * @param index  the index of the profile
     * @param text   the text to display
     * @param player the player to make the profiles for
     */
    public abstract void createProfiles(int index, String text, Player player);

    /**
     * Send the header and footer to a player
     *
     * @param player the player to send the header and footer to
     * @param header the header to send
     * @param footer the footer to send
     * @return the current adapter instance
     */
    public abstract TabAdapter sendHeaderFooter(Player player, String header, String footer);

    /**
     * Send an entry's data to a player
     *
     * @param player the player
     * @param axis   the axis of the entry
     * @param ping   the ping to display on the entry's position
     * @param text   the text to display on the entry's position
     * @return the current adapter instance
     */
    public abstract TabAdapter sendEntryData(Player player, int axis, int ping, String text);

    /**
     * Add fake players to the player's tablist
     *
     * @param player the player to send the fake players to
     * @return the current adapter instance
     */
    public abstract TabAdapter addFakePlayers(Player player);

    /**
     * Hide all real players from the tab
     *
     * @param player the player
     * @return the current adapter instance
     */
    public abstract TabAdapter hideRealPlayers(Player player);

    /**
     * Hide a real player from the tab
     *
     * @param player the player to hide the player from
     * @param target the player to hide
     * @return the current adapter instance
     */
    public abstract TabAdapter hidePlayer(Player player, Player target);

    /**
     * Show all real players on the tab
     *
     * @param player the player
     * @return the current adapter instance
     */
    public abstract TabAdapter showRealPlayers(Player player);

    /**
     * Show a real player to a player
     *
     * @param player the player
     * @param target the player to show to the other player
     * @return the current adapter instance
     */
    public abstract TabAdapter showPlayer(Player player, Player target);

}