package kamene.core;

import java.io.Serializable;

public class Stone implements Serializable {
    private final int value;
    private int row;
    private int column;

    public Stone(int value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }

    public int getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
