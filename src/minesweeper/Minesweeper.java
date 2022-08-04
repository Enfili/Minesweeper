package minesweeper;

import entity.Comment;
import entity.Score;
import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.TooManyMinesException;
import service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.Instant;

/**
 * Main application class.
 */
public class Minesweeper {
    private long startMillis;
    private BestTimes bestTimes = new BestTimes();
    private Settings setting;
    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceFile();
    private final String GAME_NAME = "minesweeper";

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

        System.out.println("Aké je Vaše meno, záhadný hráč?");
        String playerName;
        try {
            playerName = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userInterface = new ConsoleUI();
        setting = Settings.load();
        Field field = null;
        try {
            field = new Field(setting.getRowCount(), setting.getColumnCount(), setting.getMineCount());
        } catch (TooManyMinesException e) {
            System.out.println(e.getMessage());
        }
        startMillis = System.currentTimeMillis();
        GameState gs = userInterface.newGameStarted(field);

        Long endMillis = System.currentTimeMillis();
        if (GameState.SOLVED == gs) {
            int score = field.getRowCount() * field.getColumnCount() * 10 - getPlayingSeconds(endMillis);
            System.out.println(playerName + " vyhral si so skóre: " + score);
            scoreService.addScore(new Score(GAME_NAME, playerName, score, Date.from(Instant.now())));
            bestTimes.addPlayerTime(playerName, getPlayingSeconds(endMillis));
//            System.out.println(bestTimes);
        } else {
            System.out.println(playerName + " prehral si so skóre: " + 0);
            scoreService.addScore(new Score(GAME_NAME, playerName, 0, Date.from(Instant.now())));
        }
        scoreService.getBestScores(GAME_NAME).forEach(n -> System.out.println(n.getGame() + " " + n.getUsername() + " " + n.getPoints() + " " + n.getPlayedOn()));

        System.out.println("\nAký je tvoj komentár?");
        String comment;
        try {
            comment = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commentService.addComment(new Comment(GAME_NAME, playerName, comment, Date.from(Instant.now())));
        commentService.getComments(GAME_NAME).forEach(n -> System.out.println(n.getCommentedOn() + ": " + n.getComment() + "\n" + n.getUsername()));
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
