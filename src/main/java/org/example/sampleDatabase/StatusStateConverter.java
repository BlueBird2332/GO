package org.example.sampleDatabase;

import org.example.gameEngine.Board;
import org.example.models.GameState;
import org.example.models.GameStatus;
import org.example.models.Player;

public class StatusStateConverter {

    public static GameStatus getGameStatusFromState(GameState gameState){
        var board = BoardConverter.encode(gameState.board().getBoard());
        return new GameStatus(board, gameState.nextPlayer().value(), gameState.whitesCaptured(), gameState.blacksCaptured());
    }

    public static GameState getGameStateFromStatus(GameStatus gameStatus){
        var board = new Board(BoardConverter.decode(gameStatus.getBoard()));
        return new GameState(board, gameStatus.getWhitesCaptured(), gameStatus.getBlacksCaptured(),
                Player.getPlayerFromColour(gameStatus.getNextPlayer()));
    }

}
