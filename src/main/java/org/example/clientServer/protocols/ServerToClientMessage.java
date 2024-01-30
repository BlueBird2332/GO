package org.example.clientServer.protocols;

import org.example.gameEngine.Board;
import org.example.models.GameState;
import org.example.models.Player;

import java.io.Serializable;

public class ServerToClientMessage implements Serializable {

    private final Type messageType;
    //private final Board messageBoard;
    private final GameState gameState;
    private final Player winnerOrMover;

    public ServerToClientMessage(Type messageType, GameState gameState, Player winnerOrMover){
        this.messageType = messageType;
        //this.messageBoard = board;
        this.gameState = gameState;
        this.winnerOrMover = winnerOrMover;
    }

    public Type type(){
        return this.messageType;
    }

    public Board board(){
        return this.gameState.board();
    }

    public GameState gameState() {
        return this.gameState;
    }

    public Player player() {
        return this.winnerOrMover;
    }
    public enum Type{
        MOVE_MADE,
        MOVE_FAILURE,
        MOVE_SUCCESFULL,
        OTHER_PLAYER_PASSED,
        GAME_FINISHED
    }
}
