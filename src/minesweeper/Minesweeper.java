package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.consoleui.Settings;
import minesweeper.core.TooManyMinesException;

import java.util.Set;

/**
 * Main application class.
 */
public class Minesweeper {
    private static Minesweeper instance;

    public static Minesweeper getInstance() throws TooManyMinesException {
        if (instance == null)
            new Minesweeper();
        return instance;
    }
 
    /**
     * Constructor.
     */
    private Minesweeper() throws TooManyMinesException {
        instance = this;

        final UserInterface userInterface = new ConsoleUI();
        userInterface.play();
    }

    /**
     * Main method.
     * @param args arguments
     */
    public static void main(String[] args) throws TooManyMinesException {
//        System.out.println("Hello user with name: " + System.getProperty("user.name"));
        new Minesweeper();
    }
}
