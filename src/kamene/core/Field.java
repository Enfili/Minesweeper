package kamene.core;

import java.io.Serializable;

public class Field implements Serializable {
    private static final long serialVersionUID = 2L;
    private final int rowCount;
    private final int columnCount;
    private final Stone[][] stones;
    private int emptyStoneRow;
    private int emptyStoneColumn;

    public Field(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        stones = new Stone[rowCount][columnCount];
        emptyStoneRow = rowCount - 1;
        emptyStoneColumn = columnCount - 1;
        generate();
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Stone[][] getStones() {
        return stones;
    }

    public int getEmptyStoneRow() {
        return emptyStoneRow;
    }

    public void setEmptyStoneRow(int emptyStoneRow) {
        this.emptyStoneRow = emptyStoneRow;
    }

    public int getEmptyStoneColumn() {
        return emptyStoneColumn;
    }

    public void setEmptyStoneColumn(int emptyStoneColumn) {
        this.emptyStoneColumn = emptyStoneColumn;
    }

    private void generate() {
        int value = 1;
        for (int i = 0; i < stones.length; i++) {
            for (int j = 0; j < stones[i].length; j++) {
                if (i != stones.length - 1 || j != stones[i].length - 1) {
                    stones[i][j] = new Stone(value, i, j);
                    value++;
                } else {
                    stones[i][j] = null;
                }
            }
        }
    }

    public boolean isSolved() {
        // empty stone has to be placed last on the field
        if (stones[rowCount - 1][columnCount - 1] != null)
            return false;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount - 1; j++) {
                if (i == rowCount - 1 && j + 1 == columnCount - 1)
                    return true;
                if (stones[i][j + 1] != null && stones[i][j].getValue() > stones[i][j +  1].getValue())
                    return false;
            }
        }
        return true;
    }
}
