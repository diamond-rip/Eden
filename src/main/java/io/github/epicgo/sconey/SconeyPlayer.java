package io.github.epicgo.sconey;

import com.google.common.collect.ImmutableSet;
import io.github.epicgo.sconey.element.SconeyElement;
import io.github.epicgo.sconey.element.SconeyElementAdapter;
import io.github.epicgo.sconey.element.SconeyElementMode;
import io.github.epicgo.sconey.reflection.impl.RPacketScoreboardScore;
import io.github.epicgo.sconey.reflection.impl.RPacketScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import rip.diamond.practice.util.CC;

import java.util.*;

public class SconeyPlayer {

    private final Map<String, Integer> displayedScores = new HashMap<>();
    private final Map<String, String> scorePrefixes = new HashMap<>();
    private final Map<String, String> scoreSuffixes = new HashMap<>();
    private final Set<String> sentTeamCreates = new HashSet<>();
    private final Set<String> recentlyUpdatedScores = new HashSet<>();

    private final Player player;
    private final SconeyElementAdapter adapter;
    private final Objective objective;
    private Objective healthObjective;

    public SconeyPlayer(final Player player, final SconeyElementAdapter adapter) {
        this.player = player;
        this.adapter = adapter;

        final Scoreboard scoreboard = this.getScoreboard(player);
        player.setScoreboard(scoreboard);

        this.objective = this.getObjective();
        this.healthObjective = null;
    }

    /**
     * Handle a sidebar to send to a player scoreboard
     */
    public void handleUpdate() {
        final SconeyElement boardElement = this.adapter.getElement(player);
        String title = ChatColor.translateAlternateColorCodes('&', boardElement.getTitle());
        if (title.length() > 32)
            title = title.substring(0, 32);

        List<String> lines = boardElement.getLines();
        if (lines.size() > 15) {
            final List<String> delimitedLines = new ArrayList<>();

            for (int index = 0; index < 15; index++) {
                delimitedLines.add(lines.get(index));
            }

            lines = delimitedLines;
        }

        recentlyUpdatedScores.clear();

        // Update the title if needed.
        if (!objective.getDisplayName().equals(title))
            objective.setDisplayName(title);

        // Reverse the lines because scoreboard scores are in descending order.
        if (!boardElement.getMode().isDescending())
            Collections.reverse(lines);

        int cache = boardElement.getMode().getStartNumber();
        for (int index = 0; index < lines.size(); index++) {
            final String line = lines.get(index);

            final int nextValue = (index + 1);
            final int displayValue = boardElement.getMode().isDescending() ? cache-- : cache++;

            final String[] attributes = this.splitText(ChatColor.translateAlternateColorCodes('&', line), nextValue);

            final String prefix = attributes[0];
            final String score = attributes[1];
            final String suffix = attributes[2];

            recentlyUpdatedScores.add(score);

            if (!this.sentTeamCreates.contains(score))
                this.createAndAddMember(score);


            if (!this.displayedScores.containsKey(score) || this.displayedScores.get(score) != displayValue)
                this.setScore(score, displayValue);


            if (!this.scorePrefixes.containsKey(score) || !(this.scorePrefixes.get(score)).equals(prefix) || !(this.scoreSuffixes.get(score)).equals(suffix))
                this.updateScore(score, prefix, suffix);

        }

        for (final String displayedScore : ImmutableSet.copyOf(this.displayedScores.keySet())) {
            if (this.recentlyUpdatedScores.contains(displayedScore))
                continue;

            removeScore(displayedScore);
        }
    }


    // This is here so that the score joins itself, this way
    // #updateScore will work as it should (that works on a 'player'), which technically we are adding to ourselves
    private void createAndAddMember(final String teamName) {
        final RPacketScoreboardTeam scoreboardTeamAdd = new RPacketScoreboardTeam(
                teamName, "_", "_", 0, new ArrayList<>());
        final RPacketScoreboardTeam scoreboardTeamAddMember = new RPacketScoreboardTeam(
                teamName, null, null, 3, Collections.singletonList(teamName));

        scoreboardTeamAdd.sendPacket(player);
        scoreboardTeamAddMember.sendPacket(player);

        this.sentTeamCreates.add(teamName);
    }

    private void setScore(final String teamName, int value) {
        final RPacketScoreboardScore scoreboardScore = new RPacketScoreboardScore(
                teamName, this.objective.getName(), value, RPacketScoreboardScore.EnumScoreAction.CHANGE);

        scoreboardScore.sendPacket(player);

        this.displayedScores.put(teamName, value);
    }

    private void removeScore(final String teamName) {
        final RPacketScoreboardScore scoreboardScore = new RPacketScoreboardScore(
                teamName, "", 0, RPacketScoreboardScore.EnumScoreAction.REMOVE);

        scoreboardScore.sendPacket(player);

        this.displayedScores.remove(teamName);
        this.scorePrefixes.remove(teamName);
        this.scoreSuffixes.remove(teamName);
    }

    private void updateScore(final String teamName, final String prefix, final String suffix) {
        final RPacketScoreboardTeam scoreboardTeam = new RPacketScoreboardTeam(
                teamName, prefix, suffix, 2, null);

        scoreboardTeam.sendPacket(player);

        this.scorePrefixes.put(teamName, prefix);
        this.scoreSuffixes.put(teamName, suffix);
    }

    /**
     * Split the text to display on the scoreboard
     *
     * @param text  the text to split
     * @param value the value to get by score
     * @return the split text
     */
    private String[] splitText(final String text, final int value) {
        // FrozenOrb - Here be dragons.
        // FrozenOrb - Good luck maintaining this code.
        // Actual - Fixed params prefix,suffix,name, god changed && values

        String prefix;
        String team = colorCharAt(value);
        String suffix = "";

        prefix = text;

        if (prefix.length() > 16) {
            prefix = text.substring(0, 16);

            if (prefix.charAt(15) == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 15);
                team = colorCharAt(value) + lastColors(prefix) + text.substring(15, text.length());
            } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR) {
                prefix = prefix.substring(0, 14);
                team = colorCharAt(value) + lastColors(prefix) + text.substring(14, text.length());
            } else {
                team = colorCharAt(value) + lastColors(prefix) + text.substring(16, text.length());
            }

            if (team.length() > 16) {
                team = team.substring(0, 16);

                int start = prefix.length() + (team.length() - colorCharAt(value).length() - lastColors(prefix).length());
                suffix = text.substring(start, text.length());

                if (suffix.length() > 16) {
                    suffix = suffix.substring(0, 16);
                }
            }
        }

        return new String[]{prefix, team, suffix};
    }

    /**
     * Get char format of color id
     *
     * @param colorId the color id
     * @return the color char
     */
    public String colorCharAt(final int colorId) {
        return ChatColor.COLOR_CHAR + String.valueOf(colorId / 10) + ChatColor.COLOR_CHAR + colorId % 10;
    }

    /**
     * Get the last color of the text
     *
     * @param text to get the last color
     * @return the last char color
     */
    public String lastColors(final String text) {
        final String lastColors = ChatColor.getLastColors(text);

        if (lastColors.length() > 0)
            return lastColors;

        return ChatColor.COLOR_CHAR + "r";
    }

    /**
     * Get the scoreboard of a player
     *
     * @param player the player to get the scoreboard by
     * @return the scoreboard
     */
    public Scoreboard getScoreboard(final Player player) {
        return player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())
                ? Bukkit.getScoreboardManager().getNewScoreboard()
                : player.getScoreboard();
    }

    /**
     * Get the objective for the scoreboard
     *
     * @return the found objective or a newly registered objective
     */
    public Objective getObjective() {
        Scoreboard scoreboard = getScoreboard(player);
        Objective objective = scoreboard.getObjective("Sconey");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("Sconey", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        return objective;
    }

    /**
     * Get the objective for the scoreboard
     */
    public void registerHealthObjective() {
        Scoreboard scoreboard = getScoreboard(player);
        Objective objective = scoreboard.getObjective("HealthDisplay");

        if (objective == null) {
            objective = scoreboard.registerNewObjective("HealthDisplay", "health");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(CC.RED + "❤");
        }

        this.healthObjective = objective;
    }

    /**
     * Remove/Unregister the health objective
     */
    public void unregisterHealthObjective() {
        try {
            if (this.healthObjective != null && this.healthObjective.getScoreboard() != null) {
                this.healthObjective.unregister();
                this.healthObjective = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
