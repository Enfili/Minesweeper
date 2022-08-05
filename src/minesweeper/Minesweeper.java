package minesweeper;

import entity.Comment;
import entity.Rating;
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
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();
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

        String playerName = username();
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
            System.out.println("Nesprávny vstup!");
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
            System.out.println("Aké je tvoje hodnotenie tejto čarovnej hry? (Od 1 do 5.)");
            rating = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            ratingService.setRating(new Rating(GAME_NAME, playerName, rating, Date.from(Instant.now())));
            System.out.println("Priemerné hodnotenie hry: " + ratingService.getAverageRating(GAME_NAME));
        } catch (IOException e) {
            System.out.println("Nesprávny vstup!");
        } catch (GameStudioException e) {
            System.out.println("Nepodarilo sa nastaviť rating.");
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
            System.out.println("Nesprávny vstup!");
        } catch (GameStudioException e) {
            System.out.println("Nepodarilo sa pridať komentár alebo získať komentáre z databázy.");
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
