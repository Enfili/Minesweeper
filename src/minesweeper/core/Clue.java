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
        if (getState() == State.OPEN)
            return Integer.toString(value);
        return super.toString();
    }
}
