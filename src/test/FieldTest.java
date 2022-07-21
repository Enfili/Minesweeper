package test;

import minesweeper.core.*;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class FieldTest {

    private Random randomGenerator = new Random();
    private Field field;
    private int rowCount;
    private int columnCount;
    private int minesCount;

    @ BeforeEach
    public void initTests() throws TooManyMinesException {
        rowCount = randomGenerator.nextInt(10) + 5;
        columnCount = rowCount;
//        minesCount = Math.max(1, randomGenerator.nextInt(rowCount * columnCount));
        minesCount = 3;
        field = new Field(rowCount, columnCount, minesCount);
    }

    @Test
    public void checkMinesCount() {
        int minesCounter = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (field.getTile(row, column) instanceof Mine) {
                    minesCounter++;
                }
            }
        }

        assertEquals(minesCount, minesCounter, "Field was initialized incorrectly - " +
                "a different amount of mines was counted in the field than amount given in the constructor.");
    }

    @Test
    public void checkFieldInitialization() {
        assertEquals(rowCount, field.getRowCount(), "Wrong number of rows initialized.");
        assertEquals(columnCount, field.getColumnCount(), "Wrong number of columns initialized.");
        assertEquals(GameState.PLAYING, field.getState(), "State of the game is not 'PLAYING'");
    }

    @Test
    public void checkMarkTile() {
        int row = randomGenerator.nextInt(rowCount);
        int column = randomGenerator.nextInt(columnCount);
        field.markTile(row, column);
        assertEquals(Tile.State.MARKED, field.getTile(row, column).getState(), "Tile is not marked.");

        field.markTile(row, column);
        assertEquals(Tile.State.CLOSED, field.getTile(row, column).getState(), "Tile is not unmarked.");

        field.openTile(row, column);
        assertEquals(Tile.State.OPEN, field.getTile(row, column).getState(), "Tile is not open.");

        field.markTile(row, column);
        assertEquals(Tile.State.OPEN, field.getTile(row, column).getState(), "Tile is marked after it was already opened.");
    }

    @Test
    public void checkOpenMine() {
        int row = randomGenerator.nextInt(rowCount);
        int column = randomGenerator.nextInt(columnCount);
        field.getTile(row, column).setState(Tile.State.OPEN);
        assertEquals(Tile.State.OPEN, field.getTile(row, column).getState(), "Mine has not opened.");
        assertEquals(GameState.FAILED, field.getState(), "Game has not failed even if the mine was opened.");
    }

    @Test
    public void checkOpenClue() {
        int rowValue = -1;
        int columnValue = -1;
        int rowZero = -1;
        int columnZero = -1;
        boolean value = false;
        boolean zero = false;
        values:
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (!value && field.getTile(i, j) instanceof Clue && ((Clue) field.getTile(i, j)).getValue() > 0) {
                    rowValue = i;
                    columnValue = j;
                    value = true;
                } else if (field.getTile(i, j) instanceof Clue && ((Clue) field.getTile(i, j)).getValue() == 0) {
                    rowZero = i;
                    columnZero = j;
                    zero = true;
                }
                if (value && zero)
                    break values;
            }
        }

        if (rowValue != -1) {
            field.openTile(rowValue, columnValue);
            int openTiles = 0;
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    if (field.getTile(i, j).getState() == Tile.State.OPEN)
                        openTiles++;
                }
            }
            assertEquals(1, openTiles, "Wrong number of opened tiles.");
            assertEquals(GameState.PLAYING, field.getState(), "State of the game is not 'PLAYING' after one tile with nonzero value was open.");
        }

        if (rowZero != -1) {
            field.openTile(rowZero, columnZero);
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    if (field.getTile(i, j).getState() == Tile.State.OPEN)
                        assertEquals(Clue.class, field.getTile(i, j).getClass(), "While opening zero value tile, also not Clue type tiles were open.");
                }
            }
            assertEquals(GameState.PLAYING, field.getState(), "State of the game is not 'PLAYING' after opening tile with zero value.");
        }

        int rowMark = 0;
        int columnMark = 0;
        while (field.getTile(rowMark, columnMark).getState() != Tile.State.CLOSED) {
            rowMark = randomGenerator.nextInt(rowCount);
            columnMark = randomGenerator.nextInt(columnCount);
        }
        field.markTile(rowMark, columnMark);
        field.getTile(rowMark, columnMark);
        assertEquals(Tile.State.MARKED, field.getTile(rowMark, columnMark).getState(), "Tile was open after it was already marked.");
    }

    @Test
    public void fieldWithTooManyMines() {
        Field fieldWithTooManyMines = null;
        int higherMineCount = rowCount * columnCount + randomGenerator.nextInt(10) + 1;
        try {
            fieldWithTooManyMines = new Field(rowCount, columnCount, higherMineCount);
        } catch (Exception e) {
            // field with more mines than tiles should not be created - it may fail on exception
        }
        assertTrue((fieldWithTooManyMines == null) || (fieldWithTooManyMines.getMineCount() <= (rowCount * columnCount)));
    }

    // ... dalsie testy
}