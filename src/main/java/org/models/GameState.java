package org.models;

import org.Board.Board;

public record GameState(Board board, int whitesCaptured, int blacksCaptured, Player nextPlayer) {
    public static GameState getDeepCopy(GameState toCopy){
        return new GameState(new Board(toCopy.board.copyBoard()), toCopy.whitesCaptured, toCopy.blacksCaptured, toCopy.nextPlayer);
    }
    public void printCurrentState(){
        System.out.println("Current State");
        this.board.printBoard();
    }
}
