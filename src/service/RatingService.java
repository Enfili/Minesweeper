package service;

import entity.Rating;

public interface RatingService {

    void setRating(Rating rating);

    int getAverageRating(String game);

    int getRating(String game, String username);

    void reset();
}
