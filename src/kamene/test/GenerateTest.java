package kamene.test;

import kamene.core.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

public class GenerateTest {
    private Field field;

    @BeforeEach
    public void initTest() {
        Random r = new Random();
        field = new Field(r.nextInt(10) + 1, r.nextInt(10) + 1);
    }

    @Test
    public void checkGenerateTest() {
        assertTrue(isSolved(), "Field must be generated as solved.");
    }

    private boolean isSolved() {
        int rowCount = field.getRowCount();
        int columnCount = field.getColumnCount();
        // empty stone has to be placed last on the field
        if (field.getStones()[rowCount - 1][columnCount - 1] != null)
            return false;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount - 1; j++) {
                if (i == rowCount - 1 && j + 1 == columnCount - 1)
                    return true;
                if (field.getStones()[i][j + 1] != null && field.getStones()[i][j].getValue() > field.getStones()[i][j +  1].getValue())
                    return false;
            }
        }
        return true;
    }
}
