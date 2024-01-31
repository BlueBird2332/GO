package org.example.sampleDatabase;

import org.example.models.GameState;

import java.util.List;

public interface IDataBaseUtil {
    public List<GameState> getGame(long GameId);
    public GameState getSingleMove(long GameId, int moveId);

    public  void addMove(long GameId, GameState gameState);
}
