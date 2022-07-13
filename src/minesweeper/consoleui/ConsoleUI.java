package minesweeper.consoleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import minesweeper.UserInterface;
import minesweeper.core.Field;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface {
    /** Playing field. */
    private Field field;
    
    /** Input reader. */
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    
    /**
     * Reads line of text from the reader.
     * @return line as a string
     */
    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Starts the game.
     * @param field field of mines and clues
     */
    @Override
    public void newGameStarted(Field field) {
        this.field = field;
        do {
            update();
            processInput();
//            throw new UnsupportedOperationException("Resolve the game state - winning or loosing condition.");
        } while(true);
    }
    
    /**
     * Updates user interface - prints the field.
     */
    @Override
    public void update() {
        int columnCount = field.getColumnCount();
        int rowCount = field.getRowCount();

        System.out.print("  ");
        for (int i = 0; i < columnCount; i++)
            System.out.printf("%d ", i);
        System.out.println();
        int letter = 65;
        for (int i = 0; i < rowCount; i++) {
            System.out.print((char) (letter + i) + " ");
            for (int j = 0; j < columnCount; j++) {
                System.out.printf("%s ", field.getTile(i, j));
            }
            System.out.println();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Processes user input.
     * Reads line from console and does the action on a playing field according to input string.
     */
    private void processInput() {
//        throw new UnsupportedOperationException("Method processInput not yet implemented");
    }
}
