package kamene;

import kamene.core.*;
import kamene.userInterface.*;
import kamene.times.*;
import service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Puzzle {
    ConsoleUI ui;
    Field field;


    private final String GAME_NAME = "kamene";

    public Puzzle() {
        final ScoreService scoreService = new ScoreServiceJDBC();
        final CommentService commentService = new CommentServiceJDBC();
        final RatingService ratingService = new RatingServiceJDBC();

        ui = new ConsoleUI();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.println("Do you wish to start (new) game or (load) previous one or have a look at best (times) or (comments) or average (rating) of the game or you can (exit) game?");
            int rows = 0;
            int columns = 0;
            try {
                String input = br.readLine().trim().toLowerCase();

                if (input.equals("new")) {
                    while (rows <= 0 || columns <= 0) {
                        System.out.println("Enter number of rows: ");
                        rows = Integer.parseInt(br.readLine());
                        System.out.println("Enter number of columns: ");
                        columns = Integer.parseInt(br.readLine());
                        if (rows <= 0)
                            System.out.println("You need to have more rows to be able to enjoy this game. Try again.");
                        if (columns <= 0)
                            System.out.println("You need to have more columns to be able to enjoy this game. Try again.");
                    }
                    field = new Field(rows, columns);
                    ui.newGame(field);
                } else if (input.equals("load")) {
                    ui.newGame();
                } else if (input.equals("times")) {
                    scoreService.getBestScores(GAME_NAME).forEach(n -> System.out.println(n.getGame() + " " + n.getUsername() + " " + n.getPoints() + " " + n.getPlayedOn()));
//                    BestTimes bt = new BestTimes();
//                    bt.loadTimes();
//                    System.out.println(bt);
                } else if (input.equals("comments")) {
                    commentService.getComments(GAME_NAME).forEach(n -> System.out.println(n.getCommentedOn() + ": " + n.getComment() + "\n" + n.getUsername()));
                } else if (input.equals("rating")) {
                    System.out.printf("Average rating of the game is: %d%n", ratingService.getAverageRating(GAME_NAME));
                } else if (input.equals("exit")) {
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (GameStudioException e) {
                System.out.println("Unable to access database! (" + e.getMessage() + ")");
            }
        }while (true);
    }

    public static void main(String[] args) {
        new Puzzle();
    }
}
