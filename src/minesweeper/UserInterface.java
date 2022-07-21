package minesweeper;

import minesweeper.core.Field;
import minesweeper.core.TooManyMinesException;

public interface UserInterface {
    void newGameStarted(Field field) throws TooManyMinesException;

    void update() throws TooManyMinesException;
}
