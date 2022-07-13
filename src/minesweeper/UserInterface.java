package minesweeper;

import minesweeper.core.Field;

public interface UserInterface {
    void newGameStarted(Field field);

    void update();
}
