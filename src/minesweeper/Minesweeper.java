package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;

/**
 * Main application class.
 */
public class Minesweeper {
    private long startMillis;
    private BestTimes bestTimes = new BestTimes();

    /** User interface. */
    private UserInterface userInterface;
 
    /**
     * Constructor.
     */
    private Minesweeper() {
        userInterface = new ConsoleUI();
        
        Field field = new Field(9, 9, 10);
        startMillis = System.currentTimeMillis();
        userInterface.newGameStarted(field);
    }

    public int getPlayingSeconds() {
        return (int) (System.currentTimeMillis() - startMillis) / 1000;
    }

    public BestTimes getBestTimes() {
        return bestTimes;
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
