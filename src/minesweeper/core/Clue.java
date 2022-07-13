package minesweeper.core;

/**
 * Clue tile.
 */
public class Clue extends Tile {
    /** Value of the clue. */
    private final int value;
    
    /**
     * Constructor.
     * @param value  value of the clue
     */
    public Clue(int value) {
        this.value = value;
    }

    /**
     * getter for value
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (getState() == State.CLOSED)
            return super.toString();
        else if (getState() == State.MARKED)
            return "M";
        return Integer.toString(value);
    }
}
