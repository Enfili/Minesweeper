package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.TooManyMinesException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main application class.
 */
public class Minesweeper {
    private long startMillis;
    private BestTimes bestTimes = new BestTimes();
    private Settings setting;

    private static Minesweeper instance;

    public static Minesweeper getInstance() throws TooManyMinesException {
        if (instance == null)
            new Minesweeper();
        return instance;
    }

    /** User interface. */
    private UserInterface userInterface;
 
    /**
     * Constructor.
     */
    private Minesweeper() throws TooManyMinesException {
        instance = this;

        userInterface = new ConsoleUI();
        setting = Settings.load();
        Field field = null;
        try {
            field = new Field(setting.getRowCount(), setting.getColumnCount(), setting.getMineCount());
        } catch (TooManyMinesException e) {
            System.out.println(e.getMessage());
        }
        startMillis = System.currentTimeMillis();
        userInterface.newGameStarted(field);

        Long endMillis = System.currentTimeMillis();
        if (GameState.SOLVED == field.getState()) {
            System.out.println("Aké je Vaše meno, záhadný víťaz?");
            String playerName;
            try {
                playerName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bestTimes.addPlayerTime(playerName, getPlayingSeconds(endMillis));
            System.out.println(bestTimes);
        }
    }

    public int getPlayingSeconds(long endMillis) {
        return (int) (endMillis - startMillis) / 1000;
    }

    public BestTimes getBestTimes() {
        return bestTimes;
    }

    public Settings getSetting() {
        return setting;
    }

    public void setSetting(Settings setting) {
        this.setting = setting;
        setting.save();
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
