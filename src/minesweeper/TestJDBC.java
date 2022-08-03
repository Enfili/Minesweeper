package minesweeper;

import entity.Score;
import service.ScoreService;
import service.ScoreServiceJDBC;

import java.util.Date;

public class TestJDBC {

    public static void main(String[] args) throws Exception {
        ScoreService scoreService = new ScoreServiceJDBC();
        scoreService.addScore(new Score("minesweeper", "David", 456, new Date()));
        var scores = scoreService.getBestScores("minesweeper");
        System.out.println(scores);
    }
}
