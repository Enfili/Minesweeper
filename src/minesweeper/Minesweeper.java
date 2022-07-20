package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main application class.
 */
public class Minesweeper {
    private long startMillis;
    private BestTimes bestTimes = new BestTimes();

    private static Minesweeper instance;

    public static Minesweeper getInstance() {
        if (instance == null)
            new Minesweeper();
        return instance;
    }

    /** User interface. */
    private UserInterface userInterface;
 
    /**
     * Constructor.
     */
    private Minesweeper() {
        instance = this;

        userInterface = new ConsoleUI();
        Field field = new Field(9, 9, 1);
        startMillis = System.currentTimeMillis();
        userInterface.newGameStarted(field);

        System.out.println("What is your name, mysterious winner?");
        String playerName = null;
        try {
            playerName = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bestTimes.addPlayerTime(playerName, getPlayingSeconds());
        System.out.println(bestTimes);
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
