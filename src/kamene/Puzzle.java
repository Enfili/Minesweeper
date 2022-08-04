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

    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceFile();
    private final String GAME_NAME = "kamene";

    public Puzzle() {
        ui = new ConsoleUI();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.println("Do you wish to start (new) game or (load) previous one or have a look at best (times) or (comments) or you can (exit) game?");
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
                } else if (input.equals("exit")) {
                    System.exit(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }while (true);
    }

    public static void main(String[] args) {
        new Puzzle();
    }
}
