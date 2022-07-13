package minesweeper.core;

/**
 * Mine tile.
 */
public class Mine extends Tile {

    @Override
    public String toString() {
        if (getState() == State.CLOSED)
            return super.toString();
        else if (getState() == State.MARKED)
            return "M";
        return "*";
    }
}
