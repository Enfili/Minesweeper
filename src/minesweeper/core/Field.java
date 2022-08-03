package minesweeper.core;

import minesweeper.Minesweeper;

import java.util.Random;

/**
 * Field represents playing field and game logic.
 */
public class Field {
    /**
     * Playing field tiles.
     */
    private final Tile[][] tiles;

    /**
     * Field row count. Rows are indexed from 0 to (rowCount - 1).
     */
    private final int rowCount;

    /**
     * Column count. Columns are indexed from 0 to (columnCount - 1).
     */
    private final int columnCount;

    /**
     * Mine count.
     */
    private final int mineCount;

    /**
     * Game state.
     */
    private GameState state = GameState.PLAYING;

    private long startMillis;

    /**
     * Constructor.
     *
     * @param rowCount    row count
     * @param columnCount column count
     * @param mineCount   mine count
     */
    public Field(int rowCount, int columnCount, int mineCount) throws TooManyMinesException {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        if (mineCount <= columnCount * rowCount) {
            this.mineCount = mineCount;
            tiles = new Tile[rowCount][columnCount];
            generate();
        } else
            throw new TooManyMinesException("Viac mín ako políčok.");
    }

    /**
     * getters for rowCount, columnCount, mineCount, state of the game
     * and concrete tile
     */

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getMineCount() {
        return mineCount;
    }

    public GameState getState() {
        return state;
    }

    public Tile getTile(int row, int column) {
        return tiles[row][column];
    }

    /**
     * Opens tile at specified indeces.
     *
     * @param row    row number
     * @param column column number
     */
    public void openTile(int row, int column) {
        Tile tile = tiles[row][column];
        if (tile.getState() == Tile.State.CLOSED) {
            tile.setState(Tile.State.OPEN);
            if (tile instanceof Mine) {
                state = GameState.FAILED;
                return;
            } else if (tile instanceof Clue && ((Clue) tile).getValue() == 0)
                openAdjacentTiles(row, column);

            if (isSolved()) {
                state = GameState.SOLVED;
            }
        }
    }

    /**
     * Marks tile at specified indeces.
     *
     * @param row    row number
     * @param column column number
     */
    public void markTile(int row, int column) {
        Tile tile = tiles[row][column];
        if (tile.getState() == Tile.State.CLOSED) {
            tile.setState(Tile.State.MARKED);
        } else if (tile.getState() == Tile.State.MARKED) {
            tile.setState(Tile.State.CLOSED);
        }
    }

    /**
     * Generates playing field.
     */
    private void generate() {
        int nbOfMines = 0;
        Random r = new Random();
        while (nbOfMines < mineCount) {
            int row = r.nextInt(rowCount);
            int column = r.nextInt(columnCount);
//            tiles[row][column] = new Mine();
//            nbOfMines++;
            if (tiles[row][column] == null) {
                tiles[row][column] = new Mine();
                nbOfMines++;
            }
        }

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (tiles[i][j] == null) {
                    tiles[i][j] = new Clue(countAdjacentMines(i, j));
                }
            }
        }

        startMillis = System.currentTimeMillis();
    }

    /**
     * Returns true if game is solved, false otherwise.
     *
     * @return true if game is solved, false otherwise
     */
    private boolean isSolved() {
        return (rowCount * columnCount) - mineCount == getNumberOf(Tile.State.OPEN);
    }

    private int getNumberOf(Tile.State State) {
        int tilesInState = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (tiles[i][j].getState() == State)
                    tilesInState++;
            }
        }
        return tilesInState;
    }

    /**
     * Returns number of adjacent mines for a tile at specified position in the field.
     *
     * @param row    row number.
     * @param column column number.
     * @return number of adjacent mines.
     */
    private int countAdjacentMines(int row, int column) {
        int count = 0;
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int actRow = row + rowOffset;
            if (actRow >= 0 && actRow < rowCount) {
                for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
                    int actColumn = column + columnOffset;
                    if (actColumn >= 0 && actColumn < columnCount) {
                        if (tiles[actRow][actColumn] instanceof Mine) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    public int getRemainingMineCount() {
        int markedTiles = getNumberOf(Tile.State.MARKED);
        return mineCount - markedTiles;
    }

    private void openAdjacentTiles(int row, int column) {
        for (int i = row - 1; i <= row + 1; i++) {
            if (i < 0 || i >= rowCount)
                continue;
            for (int j = column - 1; j <= column + 1; j++) {
                if (j < 0 || j >= columnCount)
                    continue;

                openTile(i, j);
            }
        }
    }
}
