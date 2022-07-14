package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;

/**
 * Main application class.
 */
public class Minesweeper {
    /** User interface. */
    private UserInterface userInterface;
 
    /**
     * Constructor.
     */
    private Minesweeper() {
        userInterface = new ConsoleUI();
        
        Field field = new Field(9, 9, 0);
        userInterface.newGameStarted(field);
    }

    /**
     * Main method.
     * @param args arguments
     */
    public static void main(String[] args) {
//        System.out.println("Hello user with name: " + System.getProperty("user.name"));
        new Minesweeper();
    }
}
