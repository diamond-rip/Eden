package io.github.epicgo.sconey.element;


import lombok.Getter;

@Getter
public enum SconeyElementMode {

    DOWN(true, 15),
    NEGATIVE(true, -1),
    UP(false, 1),
    CUSTOM(false, 0);

    private boolean descending;
    private int startNumber;

    /**
     * Constructor a new SconeyElementMode instance
     *
     * @param descending  whether the positions are going down or up.
     * @param startNumber from where to loop from.
     */
    private SconeyElementMode(final boolean descending, final int startNumber) {
        this.descending = descending;
        this.startNumber = startNumber;
    }

    public SconeyElementMode reverse() {
        return descending(!this.descending);
    }

    public SconeyElementMode descending(boolean descending) {
        this.descending = descending;
        return this;
    }

    public SconeyElementMode startNumber(int startNumber) {
        this.startNumber = startNumber;
        return this;
    }
}
