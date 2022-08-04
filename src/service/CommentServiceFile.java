package service;

import entity.Comment;
import entity.Score;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentServiceFile implements CommentService {
    private List<Comment> comments = new ArrayList<>();
    private static final String FILE = "comments.bin";

    @Override
    public void addComment(Comment comment) {
        comments = load();
        comments.add(comment);
        save(comments);
    }

    @Override
    public List<Comment> getComments(String game) {
        return comments
                .stream()
                .filter(s -> s.getGame().equals(game))
                .sorted((d1, d2) -> -d1.getCommentedOn().compareTo(d2.getCommentedOn()))
                .collect(Collectors.toList());
    }

    @Override
    public void reset() {
        comments = new ArrayList<>();
        save(comments);
    }

    private void save(List<Comment> scoresToSave) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(scoresToSave);
        } catch (IOException e) {
            throw new GameStudioException(e);
        }
    }

    private List<Comment> load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (List<Comment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameStudioException(e);
        }
    }
}
