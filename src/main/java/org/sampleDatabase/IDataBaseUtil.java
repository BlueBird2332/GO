package org.sampleDatabase;

import org.models.GameState;

import java.util.List;

public interface IDataBaseUtil {
    public List<GameState> getGame(int GameId);
    public GameState getSingleMove(int GameId, int moveId);

    public  void addMove(int GameId, GameState gameState);
}
