package minesweeper;

import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.TooManyMinesException;

public interface UserInterface {
    GameState newGameStarted(Field field) throws TooManyMinesException;

    void update() throws TooManyMinesException;

    void play() throws TooManyMinesException;
}
