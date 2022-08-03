package minesweeper.consoleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minesweeper.Minesweeper;
import minesweeper.Settings;
import minesweeper.UserInterface;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.TooManyMinesException;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface {
    public static final int LETTER_ASCII = 65;
    public static final Pattern PATTERN = Pattern.compile("([moMO])([a-zA-Z])(-?\\d+)");
    /** Playing field. */
    private Field field;

    private String format = "%3s";

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
    public GameState newGameStarted(Field field) throws TooManyMinesException {
        this.field = field;
        this.format = "%" + (String.valueOf(field.getColumnCount()).length() + 1) + "s";

        System.out.println("Chceš si vybrať obtiažnosť?");
        System.out.println("(0) NIE (1) BEGINNER, (2) INTERMEDIATE, (3) EXPERT");
        String difficulty = readLine();
        if (difficulty != null && !difficulty.equals("")) {
            try {
                int level = Integer.parseInt(difficulty);
                if (level != 0) {
                    Settings s = null;
                    switch (level) {
                        case 1 -> s = Settings.BEGINNER;
                        case 2 -> s = Settings.INTERMEDIATE;
                        case 3 -> s = Settings.EXPERT;
                    }

                    if (s != null) {
                        Minesweeper.getInstance().setSetting(s);
                        this.field = new Field(s.getRowCount(), s.getColumnCount(), s.getMineCount());
                    }
                }
            } catch (NumberFormatException e) {
                // empty on purpose
            }
        }

        update();
        do {
            processInput();
            update();
            if ((this.field.getState() == GameState.SOLVED)) {
                System.out.println("Si víťaz!");
                break;
//                System.exit(0);
            } else if ((this.field.getState() == GameState.FAILED)) {
                System.out.println("Prehral si a mal by si sa hanbiť!");
                break;
//                System.exit(0);
            }
        } while(true);
        return this.field.getState();
    }
    
    /**
     * Updates user interface - prints the field.
     */
    @Override
    public void update() throws TooManyMinesException {
        int columnCount = field.getColumnCount();
        int rowCount = field.getRowCount();

        System.out.println("Remaining number of mines: " + field.getRemainingMineCount());

        System.out.print("  ");
        for (int i = 0; i < columnCount; i++)
            System.out.printf("%3d", i);
        System.out.println();
        for (int i = 0; i < rowCount; i++) {
            System.out.printf("%2c", (LETTER_ASCII + i));
            for (int j = 0; j < columnCount; j++) {
                System.out.printf(format, field.getTile(i, j));
            }
            System.out.println();
        }

        Long curTime = System.currentTimeMillis();
        System.out.printf("Hráš už %d sekúnd.%n", Minesweeper.getInstance().getPlayingSeconds(curTime));
    }
    
    /**
     * Processes user input.
     * Reads line from console and does the action on a playing field according to input string.
     */
    private void processInput() {
        System.out.println("X – ukončenie hry, MA1 – označenie dlaždice v riadku A a stĺpci 1, OB4 – odkrytie dlaždice v riadku B a stĺpci 4");
        String input = readLine().trim().toUpperCase();
        try {
            handleInput(input);
        } catch (WrongFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleInput(String input) throws WrongFormatException {
        if (input.equals("X")) {
            System.exit(0);
        }

        Matcher matcher = PATTERN.matcher(input);

        if (!matcher.matches()) {
            throw new WrongFormatException("Neplatný ťah!");
        }

        for (int i = 1; i <= matcher.groupCount(); i++) {
            System.out.println(matcher.group(i));
        }
        int row = matcher.group(2).charAt(0) - LETTER_ASCII;
        int column = Integer.parseInt(matcher.group(3));

        if (row < 0 || row >= field.getRowCount() || column < 0 || column >= field.getColumnCount())
            throw new WrongFormatException("Zadal si ťah mimo poľa!");

        if (matcher.group(1).equals("M")) {
            field.markTile(row, column);
        } else {
            field.openTile(row, column);
        }
    }
}
