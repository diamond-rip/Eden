package io.github.epicgo.sconey.element;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides helper methods to create an elements containing the provided scoreboard
 */
@Getter
@Setter
public class SconeyElement {

    /**
    * List of format strings to place the scoreboard line on
    */
    private final List<String> lines = new ArrayList<>();

    /**
     * The format string to display on the scoreboard title
     */
    private String title;
    /**
     * The position numbers being displayed on the right column of the scoreboard
     */
    private SconeyElementMode mode = SconeyElementMode.UP;

    /**
     * Add a new line to the scoreboard element
     *
     * @param text the line to display
     */
    public void add(final String text) {
        this.lines.add(text);
    }

    /**
     * Add a new line to the scoreboard element on a certain spot in the list
     *
     * @param position  the index to place the line on
     * @param text the line to provide
     */
    public void add(final int position, final String text) {
        if (position > 16) return;

        this.lines.add(position, text);
    }

    /**
     * Add list of lines to the scoreboard element
     *
     * @param texts all the lines to display
     */
    public void addAll(final List<String> texts) {
        this.lines.addAll(texts);
    }
}