package org.models;

import org.gameEngine.Board;

import java.io.Serializable;

public record GameState(Board board, int whitesCaptured, int blacksCaptured, Player nextPlayer) implements Serializable {
    public static GameState getDeepCopy(GameState toCopy){
        return new GameState(new Board(toCopy.board.copyBoard()), toCopy.whitesCaptured, toCopy.blacksCaptured, toCopy.nextPlayer);
    }
    public void printCurrentState(){
        this.board.printBoard();
        System.out.println("White captured = " + this.whitesCaptured);
        System.out.println("Black captured = " + this.blacksCaptured);
        System.out.println(nextPlayer);
    }
}
