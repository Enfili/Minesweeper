package kamene.userInterface;

import entity.Comment;
import entity.Rating;
import entity.Score;
import kamene.core.Field;
import kamene.core.Stone;

import kamene.times.*;
import service.*;

import java.io.*;
import java.sql.Date;
import java.time.Instant;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    Field field;
    private int widthOfEmptySpaces;
    private String[] moves = {"w", "a", "s", "d"};
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final Pattern INPUT = Pattern.compile("[wasd]|up|down|left|right");
    private long startTime;
//    private BestTimes bestTimes = new BestTimes();

    private final ScoreService scoreService = new ScoreServiceJDBC();
    private final CommentService commentService = new CommentServiceJDBC();
    private final RatingService ratingService = new RatingServiceJDBC();
    private final String GAME_NAME = "kamene";

    public void newGame(Field field) {
        this.field = field;
        widthOfEmptySpaces = String.valueOf(field.getColumnCount() * field.getRowCount()).length() + 1;
        shuffle(2);
        runGame();
    }

    // constructor for loading game
    public void newGame() {
        field = loadGame();
        if (field == null) {
            System.out.println("No saved game. You have to start new one.");
            int rows = 0;
            int columns = 0;
            while (rows <= 0 || columns <= 0) {
                try {
                    System.out.println("Enter number of rows: ");
                    rows = Integer.parseInt(br.readLine());
                    System.out.println("Enter number of columns: ");
                    columns = Integer.parseInt(br.readLine());
                    if (rows <= 0)
                        System.out.println("You need to have more rows to be able to enjoy this game. Try again.");
                    if (columns <= 0)
                        System.out.println("You need to have more columns to be able to enjoy this game. Try again.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        widthOfEmptySpaces = String.valueOf(field.getColumnCount() * field.getRowCount()).length() + 1;
        runGame();
    }

    private void runGame() {
        startTime = System.currentTimeMillis();
        display();
        do {
            if (field.isSolved()) {
                finishGame();
            }
            processInput();
            display();
            System.out.printf("%d seconds has elapsed since you have chosen this eternal journey.%n",
                    (int) (System.currentTimeMillis() - startTime) / 1000);
        } while (true);
    }

    private void finishGame() {
        System.out.println("Congratulations. YOU ARE A WINNER!");
        int gameTime = (int) (System.currentTimeMillis() - startTime) / 1000;
        System.out.printf("It took you %d seconds to conquer this puzzle!%n", gameTime);
        String name = username();
        try {
            scoreService.addScore(new Score(GAME_NAME, name, gameTime, Date.from(Instant.now())));
        } catch (GameStudioException e) {
            System.out.println("Unable to access the database. (" + e.getMessage() + ")");
        }
        handleAddComments(name);
        handleAddRating(name);

        do {
            System.out.println("Do you wish to start (new) game or (exit) game or have a look at best (times) or (comments) or average (rating) of the game?");
            try {
                String input = br.readLine().trim().toLowerCase();
                if (input.equals("exit")) {
//                    bestTimes.savePlayingTimes();
                    System.exit(0);
                } else if (input.equals("new")) {
                    newGame(new Field(this.field.getRowCount(), this.field.getColumnCount()));
                    return;
                } else if (input.equals("times")) {
                    scoreService.getBestScores(GAME_NAME).forEach(n -> System.out.println(n.getGame() + " " + n.getUsername() + " " + n.getPoints() + " " + n.getPlayedOn()));
//                    System.out.println(bestTimes);
                } else if (input.equals("comments")) {
                    commentService.getComments(GAME_NAME).forEach(n -> System.out.println(n.getCommentedOn() + ": " + n.getComment() + "\n" + n.getUsername()));
                } else if (input.equals("rating")) {
                    System.out.printf("Average rating of the game is: %d%n", ratingService.getAverageRating(GAME_NAME));
                } else {
                    System.out.println("Your input is not correct. Choose again and choose wisely!");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (GameStudioException e) {
                System.out.println("Unable to access database! (" + e.getMessage() + ")");
            }
        } while(true);
    }

    private String username() {
        StringBuilder playerName = new StringBuilder();
        try {
            while (playerName.isEmpty() || playerName.length() > 64) {
                if (!playerName.isEmpty()) {
                    playerName.delete(0, playerName.length());
                }
                System.out.println("What is your name, oh, mysterious player?");
                playerName.append(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            return playerName.toString();
        } catch (IOException e) {
            System.out.println("Incorrect input! (" + e.getMessage() + ")");
        }
        return null;
    }

    private void handleAddRating(String playerName) {
        int rating = 0;
        try {
            while (rating < 1 || rating > 5) {
                System.out.println("How do you rate this magnificient game? (5 - the best, 1 - the worst)");
                rating = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            ratingService.setRating(new Rating(GAME_NAME, playerName, rating, Date.from(Instant.now())));
        } catch (IOException e) {
            System.out.println("Incorrect input! (" + e.getMessage() + ")");
        } catch (GameStudioException e) {
            System.out.println("Unable to set rating in the database. (" + e.getMessage() + ")");
        }
    }

    private void handleAddComments(String playerName) {
        StringBuilder comment = new StringBuilder();
        try {
            while (comment.isEmpty() || comment.length() > 1000) {
                if (!comment.isEmpty()) {
                    comment.delete(0, comment.length());
                }
                System.out.println("Do you have anything else on your mind?");
                comment.append(new BufferedReader(new InputStreamReader(System.in)).readLine());
            }
            commentService.addComment(new Comment(GAME_NAME, playerName, comment.toString(), Date.from(Instant.now())));
        } catch (IOException e) {
            System.out.println("Incorrect input! (" + e.getMessage() + ")");
        } catch (GameStudioException e) {
            System.out.println("Unable to add comment to the database. (" + e.getMessage() + ")");
        }
    }

    private void processInput() {
        System.out.println("Choose move: w (up), s (down), a (left), d (right). Or (save) game or start (new) game or (exit) game.");
        try {
            String input = br.readLine().trim().toLowerCase();
            try {
                checkInput(input);
                shiftStone(input);
            } catch (WrongInputFormatException | MoveOutOfFieldException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkInput(String input) throws WrongInputFormatException {
        if (input.equals("exit"))
            System.exit(0);
        else if (input.equals("new")) {
            newGame(new Field(this.field.getRowCount(), this.field.getColumnCount()));
            return;
        } else if (input.equals("save")) {
            saveGame();
            System.out.println("Your game has been saved.");
            return;
        }

        Matcher matcher = INPUT.matcher(input);
        if (!matcher.matches())
            throw new WrongInputFormatException("Unknown move or command.");
    }

    private void display() {
        for (int i = 0; i < field.getRowCount(); i++) {
            for (int j = 0; j < field.getColumnCount(); j++) {
                if (field.getStones()[i][j] != null)
                    System.out.printf("%" + widthOfEmptySpaces + "d", field.getStones()[i][j].getValue());
                else
                    System.out.printf("%" + widthOfEmptySpaces + "s", " ");
            }
            System.out.println();
        }
    }

    private void shuffle(int nbOfShifts) {
        Random r = new Random();
        for (int i = 0; i < nbOfShifts; i++) {
            try {
                shiftStone(moves[r.nextInt(4)]);
            } catch (MoveOutOfFieldException e) {
                // intentionally left empty
            }
        }
    }

    private void shiftStone(String shift) throws MoveOutOfFieldException {
        int emptyRow = field.getEmptyStoneRow();
        int emptyColumn = field.getEmptyStoneColumn();
        switch (shift) {
            case "w":
            case "up":
                if (emptyRow + 1 < field.getRowCount()) {
                    swapStones(field.getStones()[emptyRow + 1][emptyColumn]);
                } else {
                    throw new MoveOutOfFieldException("Out of this field move.");
                }
                break;
            case "a":
            case "left":
                if (emptyColumn + 1 < field.getColumnCount()) {
                    swapStones(field.getStones()[emptyRow][emptyColumn + 1]);
                } else {
                    throw new MoveOutOfFieldException("Out of this field move.");
                }
                break;
            case "s":
            case "down":
                if (emptyRow - 1 >= 0) {
                    swapStones(field.getStones()[emptyRow - 1][emptyColumn]);
                } else {
                    throw new MoveOutOfFieldException("Out of this field move.");
                }
                break;
            case "d":
            case "right":
                if (emptyColumn - 1 >= 0) {
                    swapStones(field.getStones()[emptyRow][emptyColumn - 1]);
                } else {
                    throw new MoveOutOfFieldException("Out of this field move.");
                }
                break;
        }
    }

    private void swapStones(Stone stone) {
        field.getStones()[stone.getRow()][stone.getColumn()] = null;
        field.getStones()[field.getEmptyStoneRow()][field.getEmptyStoneColumn()] = stone;
        int newEmptyRow = stone.getRow();
        int newEmptyColumn = stone.getColumn();
        stone.setRow(field.getEmptyStoneRow());
        stone.setColumn(field.getEmptyStoneColumn());
        field.setEmptyStoneRow(newEmptyRow);
        field.setEmptyStoneColumn(newEmptyColumn);
    }

    private void saveGame() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File("previousGame.txt")));
            oos.writeObject(field);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Field loadGame() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream("previousGame.txt"));
            try {
                field = (Field) ois.readObject();
                return field;
            } catch (ClassNotFoundException e) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
