package org.sampleDatabase;

import org.Board.Board;
import org.models.GameState;
import org.models.GameStatus;
import org.models.Player;

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
