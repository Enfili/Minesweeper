package service;

import entity.Score;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreServiceFile implements ScoreService {
    private List<Score> scores = new ArrayList<>();
    private static final String FILE = "score.bin";

    @Override
    public void addScore(Score score) {
        scores = load();
        scores.add(score);
        save(scores);
    }

    @Override
    public List<Score> getBestScores(String game) {
        scores = load();
        return scores
                .stream()
                .filter(s -> s.getGame().equals(game))
                .sorted((s1, s2) -> -Integer.compare(s1.getPoints(), s2.getPoints()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public void reset() {
        scores = new ArrayList<>();
        save(scores);
    }

    private void save(List<Score> scoresToSave) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(scoresToSave);
        } catch (IOException e) {
            throw new GameStudioException(e);
        }
    }

    private List<Score> load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (List<Score>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameStudioException(e);
        }
    }
}
