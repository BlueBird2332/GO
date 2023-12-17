package org.example;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class GameEngine {
    private String[][] board ;
    private Player currentPlayer = Player.BLACK;
    public String[][] getBoard() {
        return board;
    }

    {
        initializeBoard();
    }
    public void initializeBoard() {
        this.board = new String[11][11];
        for (String[] strings : board) {
            Arrays.fill(strings, "0");
        }
    }

    public static void printBoard(String[][] board) {
        for (String[] strings : board) {
            for (String string : strings) {
                System.out.printf("%5s", string);
            }
            System.out.println();
        }
        System.out.println();
    }


    public void makeMove(int column, int row){
        if(isMoveAllowed(column, row)){
            //TO DO
            // copy the board
            if(currentPlayer == Player.BLACK){
                board[column][row] = "B";
                currentPlayer = Player.WHITE;
            } else {
                board[column][row] = "W";
                currentPlayer = Player.BLACK;
            }
        }
    }
    public boolean isMoveAllowed(int column, int row){
        if (board[column][row] != "0") {
            System.out.println("cannot make a move");
            return false;
        } else {
            return true;
        }
    }
}
