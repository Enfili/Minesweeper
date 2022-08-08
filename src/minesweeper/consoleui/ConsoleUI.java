package minesweeper.consoleui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Comment;
import entity.Rating;
import entity.Score;
import minesweeper.Minesweeper;
import minesweeper.UserInterface;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.TooManyMinesException;
import service.*;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface {
    public static final int LETTER_ASCII = 65;
    public static final Pattern PATTERN = Pattern.compile("([moMO])([a-zA-Z])(-?\\d+)");
    /** Playing field. */
    private Field field;

    private String format = "%3s";

    // originally in Minesweeper.java
    private long startMillis;
    private Settings setting;
    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();
    private final String GAME_NAME = "minesweeper";

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
                        this.setting = s;
                        this.setting.save();
//                        Minesweeper.getInstance().setSetting(s);
//                        setSetting(s);
//                        this.field = new Field(s.getRowCount(), s.getColumnCount(), s.getMineCount());
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
    public void update() {
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
        System.out.printf("Hráš už %d sekúnd.%n", ((curTime - startMillis) / 1000));
//        System.out.printf("Hráš už %d sekúnd.%n", Minesweeper.getInstance().getPlayingSeconds(curTime));
//        System.out.printf("Hráš už %d sekúnd.%n", getPlayingSeconds(curTime));
    }

    @Override
    public void play() throws TooManyMinesException {
        String playerName = username();

        setting = Settings.load();
        Field field = null;
        try {
            field = new Field(setting.getRowCount(), setting.getColumnCount(), setting.getMineCount());
        } catch (TooManyMinesException e) {
            System.out.println(e.getMessage());
        }

        startMillis = System.currentTimeMillis();
        GameState gs = newGameStarted(field);

        Long endMillis = System.currentTimeMillis();
        if (GameState.SOLVED == gs) {
            int score = field.getRowCount() * field.getColumnCount() * 10 - getPlayingSeconds(endMillis);
            System.out.println(playerName + " vyhral si so skóre: " + score);
            scoreService.addScore(new Score(GAME_NAME, playerName, score, Date.from(Instant.now())));
        } else {
            System.out.println(playerName + " prehral si so skóre: " + 0);
            scoreService.addScore(new Score(GAME_NAME, playerName, 0, Date.from(Instant.now())));
        }
        scoreService.getBestScores(GAME_NAME).forEach(n -> System.out.println(n.getGame() + " " + n.getUsername() + " " + n.getPoints() + " " + n.getPlayedOn()));

        handleComments(playerName);
        handleRating(playerName);
    }

    private String username() {
        StringBuilder playerName = new StringBuilder();
        try {
            while (playerName.isEmpty() || playerName.length() > 64) {
                if (!playerName.isEmpty()) {
                    playerName.delete(0, playerName.length());
                }
                System.out.println("Aké je Vaše meno, záhadný hráč?");
                playerName.append(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            return playerName.toString();
        } catch (IOException e) {
            System.out.println("Nesprávny vstup! (" + e.getMessage() + ")");
        }
        return null;
    }

    private void handleRating(String playerName) {
        int rating = 0;
        try {
            while (rating < 1 || rating > 5) {
                System.out.println("Aké je tvoje hodnotenie tejto čarovnej hry? (Od 1 do 5.)");
                rating = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            ratingService.setRating(new Rating(GAME_NAME, playerName, rating, Date.from(Instant.now())));
            System.out.println("Priemerné hodnotenie hry: " + ratingService.getAverageRating(GAME_NAME));
        } catch (IOException e) {
            System.out.println("Nesprávny vstup! (" + e.getMessage() + ")");
        } catch (GameStudioException e) {
            System.out.println("Nepodarilo sa nastaviť rating v databáze. (" + e.getMessage() + ")");
        }
    }

    private void handleComments(String playerName) {
        StringBuilder comment = new StringBuilder();
        try {
            while (comment.isEmpty() || comment.length() > 1000) {
                if (!comment.isEmpty()) {
                    comment.delete(0, comment.length());
                }
                System.out.println("\nAký je tvoj komentár?");
                comment.append(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            commentService.addComment(new Comment(GAME_NAME, playerName, comment.toString(), Date.from(Instant.now())));
            commentService.getComments(GAME_NAME).forEach(n -> System.out.println(n.getCommentedOn() + ": " + n.getComment() + "\n" + n.getUsername()));
        } catch (IOException e) {
            System.out.println("Nesprávny vstup! (" + e.getMessage() + ")");
        } catch (GameStudioException e) {
            System.out.println("Nepodarilo sa pridať komentár alebo získať komentáre z databázy. (" + e.getMessage() + ")");
        }
    }

    private int getPlayingSeconds(long endMillis) {
        return (int) (endMillis - startMillis) / 1000;
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
