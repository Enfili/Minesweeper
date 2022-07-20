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

    @BeforeEach
    public void initTests() {
        rowCount = randomGenerator.nextInt(10) + 5;
        columnCount = rowCount;
        minesCount = Math.max(1, randomGenerator.nextInt(rowCount * columnCount));
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
        assertEquals(Tile.State.OPEN, field.getTile(row, column).getState(), "Tile is marked after was opened.");
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
        int rowValue = 0;
        int columnValue = 0;
        values:
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (field.getTile(i, j) instanceof Clue && ((Clue) field.getTile(i, j)).getValue() > 0) {
                    rowValue = i;
                    columnValue = j;
                    break values;
                }
            }
        }

        field.openTile(rowValue, columnValue);
        int openTiles = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (field.getTile(i, j).getState() == Tile.State.OPEN)
                    openTiles++;
            }
        }
        assertEquals(1, openTiles, "Wrong number of opened tiles.");
        assertEquals(GameState.PLAYING, field.getState(), "State of the game is not 'PLAYING'");

        int rowZero = 0;
        int columnZero = 0;
        while (field.getTile(rowZero, columnZero).getState() != Tile.State.CLOSED) {
            rowZero = randomGenerator.nextInt(rowCount);
            columnZero = randomGenerator.nextInt(columnCount);
        }
        field.markTile(rowZero, columnZero);

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (field.getTile(i, j).getState() == Tile.State.OPEN) {
                    assertInstanceOf(Clue.class, field.getTile(i, j).getClass(), "Open tile is not of type Clue.");
                }
            }
        }
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