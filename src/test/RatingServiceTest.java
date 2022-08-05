package test;

import entity.Rating;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import service.GameStudioException;
import service.RatingService;
import service.RatingServiceJDBC;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RatingServiceTest {
    private RatingService ratingService = new RatingServiceJDBC();
    private static final String JDBC_URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String STATEMENT_GET_NUMBER_OF_ROWS = "SELECT COUNT(game) FROM rating";

    @Test
    public void testSetRatingDuplicity() {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.createStatement()) {
            ratingService.reset();
            Rating rating = new Rating("minesweeper", "jaro", 1, new Date());
            ratingService.setRating(rating);
            ratingService.setRating(rating);

            try (ResultSet rs = statement.executeQuery(STATEMENT_GET_NUMBER_OF_ROWS)) {
                rs.next();
                assertEquals(1, rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new GameStudioException();
        }
    }

    @Test
    public void testGetAverageRating() {
        ratingService.setRating(new Rating("minesweeper", "jaro", 2, new Date()));
        ratingService.setRating(new Rating("minesweeper", "juro", 1, new Date()));
        ratingService.setRating(new Rating("minesweeper", "jano", 4, new Date()));
        assertEquals(Math.round((2 + 1 + 4) / 3), ratingService.getAverageRating("minesweeper"));

        ratingService.reset();
        assertEquals(0, ratingService.getAverageRating("minesweeper"));
    }

    @Test
    public void testGetRating() {
        ratingService.reset();
        assertEquals(0, ratingService.getRating("minesweeper", "username"));

        ratingService.setRating(new Rating("minesweeper", "jaro", 1, new Date()));
        assertEquals(0, ratingService.getRating("minesweeper", "username"));
        assertEquals(0, ratingService.getRating("kamene", "jaro"));
        assertEquals(1, ratingService.getRating("minesweeper", "jaro"));
    }
}
