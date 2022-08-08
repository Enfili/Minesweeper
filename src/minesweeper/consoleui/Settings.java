package minesweeper.consoleui;

import java.io.*;

public class Settings implements Serializable {
    private final int rowCount, columnCount, mineCount;
    public static final String SETTING_FILE = System.getProperty("user.home") + System.getProperty("file.separator") + "minesweeper.settings";
    public static final Settings BEGINNER = new Settings(9, 9, 0);
    public static final Settings INTERMEDIATE = new Settings(16, 16, 40);
    public static final Settings EXPERT = new Settings(16, 30, 99);

    public Settings(int rowCount, int columnCount, int mineCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.mineCount = mineCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getMineCount() {
        return mineCount;
    }

    public void save() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream (new FileOutputStream(SETTING_FILE));
            oos.writeObject(this);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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

    public static Settings load() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(SETTING_FILE));
            return (Settings) ois.readObject();
        } catch (IOException e) {
            System.out.println("Nepodarilo sa nahrať settings, používam predchádzajúcu uloženú.");
        } catch (ClassNotFoundException e) {
            System.out.println("Nepodarilo sa nahrať settings, používam predchádzajúcu uloženú.");
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return BEGINNER;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Settings))
            return false;
        if (this.columnCount != ((Settings) obj).columnCount
            && this.rowCount != ((Settings) obj).rowCount
            && this.mineCount != ((Settings) obj).mineCount)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return rowCount * columnCount * mineCount;
    }
}
