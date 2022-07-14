package minesweeper.consoleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minesweeper.UserInterface;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.Tile;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface {
    public static final int LETTER_ASCII = 65;
    public static final String REGEX_INPUT = "([moMO])([a-zA-Z])(\\d+)";
    /** Playing field. */
    private Field field;

    private String format = "%2s";

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
        this.format = "%" + (String.valueOf(field.getColumnCount()).length() + 1) + "s";
        do {
            update();
            if ((field.getState() == GameState.SOLVED)) {
                System.out.println("Si víťaz!");
                System.exit(0);
            } else if ((field.getState() == GameState.FAILED)) {
                System.out.println("Prehral si a mal by si sa hanbiť!");
                System.exit(0);
            }
            processInput();
        } while(true);
    }
    
    /**
     * Updates user interface - prints the field.
     */
    @Override
    public void update() {
        int columnCount = field.getColumnCount();
        int rowCount = field.getRowCount();

        System.out.printf(format, "");
        for (int i = 0; i < columnCount; i++)
            System.out.printf("%2d", i);
        System.out.println();

        for (int i = 0; i < rowCount; i++) {
            System.out.printf("%2c", (LETTER_ASCII + i));
            for (int j = 0; j < columnCount; j++) {
                System.out.printf(format, field.getTile(i, j));
            }
            System.out.println();
        }
    }
    
    /**
     * Processes user input.
     * Reads line from console and does the action on a playing field according to input string.
     */
    private void processInput() {
        System.out.println("X – ukončenie hry, MA1 – označenie dlaždice v riadku A a stĺpci 1, OB4 – odkrytie dlaždice v riadku B a stĺpci 4");
        String input = readLine();

        if (input.equals("X")) {
            System.exit(0);
        }

        Pattern pattern = Pattern.compile(REGEX_INPUT);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            System.out.println("Neplatný ťah!");
            processInput();
        } else {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
            }
            int row = matcher.group(2).toUpperCase().charAt(0) - LETTER_ASCII;
            int column = Character.getNumericValue(matcher.group(3).toUpperCase().charAt(0));
            if (matcher.group(1).equals("M")) {
                field.markTile(row, column);
            } else {
                field.openTile(row, column);
            }
        }
    }
}
