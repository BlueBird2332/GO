package org.models;

import org.Bot.Bot;
import org.gameEngine.Board;

import java.util.List;

public record GameState(Board board, int whitesCaptured, int blacksCaptured, Player nextPlayer, List<Board> koList) {
    public static GameState getDeepCopy(GameState toCopy){
        return new GameState(new Board(toCopy.board.copyBoard()), toCopy.whitesCaptured, toCopy.blacksCaptured, toCopy.nextPlayer, toCopy.koList);
    }
    public void printCurrentState(){
        System.out.println("Current State");
        this.board.printBoard();
    }
}
