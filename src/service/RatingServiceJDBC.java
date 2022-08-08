package service;

/*
create table rating (
    game VARCHAR(64),
    username VARCHAR(64),
    rating INT,
    rated_on TIMESTAMP,
    constraint rating check (rating between 1 and 5),
    unique (game, username)
);
 */


import entity.Rating;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RatingServiceJDBC implements RatingService {
    private static final String JDBC_URL = "jdbc:postgresql://localhost/gamestudio";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private static final String STATEMENT_DELETE_RATING = "DELETE FROM rating WHERE game=? AND username=?";
    private static final String STATEMENT_GET_AVERAGE = "SELECT ROUND(AVG(rating)) FROM rating WHERE game=?";
    private static final String STATEMENT_GET_RATING = "SELECT rating FROM rating WHERE game=? AND username=?";
    private static final String RESET = "DELETE FROM rating";

    private static final String STATEMENT_SELECT_RATING = "SELECT FROM rating WHERE game=? AND username=?";
    private static final String STATEMENT_SET_RATING = "INSERT INTO rating VALUES (?, ?, ?, ?)";
    private static final String STATEMENT_UPDATE_RATING = "UPDATE rating SET rating=?, rated_on=? WHERE game=? AND username=?";

    @Override
    public void setRating(Rating rating) {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.prepareStatement(STATEMENT_SELECT_RATING))
        {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getUsername());

            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    updateRating(rating);
                } else {
                    setNewRating(rating);
                }
            }
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    private void updateRating(Rating rating) {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.prepareStatement(STATEMENT_UPDATE_RATING))
        {
            statement.setInt(1, rating.getRating());
            statement.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
            statement.setString(3, rating.getGame());
            statement.setString(4, rating.getUsername());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    private void setNewRating(Rating rating) {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.prepareStatement(STATEMENT_SET_RATING))
        {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getUsername());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.prepareStatement(STATEMENT_GET_AVERAGE)) {
            statement.setString(1, game);

            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    @Override
    public int getRating(String game, String username) {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.prepareStatement(STATEMENT_GET_RATING)) {
            statement.setString(1, game);
            statement.setString(2, username);

            try (var rs = statement.executeQuery()) {
                if (!rs.next())
                    return 0;
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    @Override
    public void reset() {
        try (var connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             var statement = connection.createStatement();)
        {
            statement.executeUpdate(RESET);
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }
}
