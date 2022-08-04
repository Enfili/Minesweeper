package kamene.times;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

public class BestTimes implements Serializable {
    private static final long serialVersionUID = 1L;
    List<PlayerTime> playerTimes = new ArrayList<>();

    public BestTimes() {
        playerTimes = loadTimes();
    }

    public void addPlayer(String name, int time) {
        playerTimes.add(new PlayerTime(name, time));
        Collections.sort(playerTimes);
    }

    public void savePlayingTimes() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File("times.txt")));
            oos.writeObject(playerTimes);
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

    public List<PlayerTime> loadTimes() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream("times.txt"));
            playerTimes = (List<PlayerTime>) ois.readObject();
            return playerTimes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String toString() {
        Formatter f = new Formatter();
        for (PlayerTime p: playerTimes)
            f.format("%s, playing time: %d%n", p.name, p.time);
        return f.toString();
    }

    private class PlayerTime implements Comparable<PlayerTime>, Serializable {
        private final String name;
        private final int time;

        public PlayerTime(String name, int time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public int compareTo(PlayerTime o) {
            if (o == null)
                return -1;
            return time - o.time;
        }
    }
}
