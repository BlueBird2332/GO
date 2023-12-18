package org.example;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public final class GameEngine {
    private Board board ;
    public void printBoard(){
        this.board.printBoard();
    }
    private int whiteCaptured = 0;
    private int blackCaptured = 0;

    {
        initializeBoard();
    }
    public void initializeBoard() {
        this.board = new Board(11);
    }
//    private int performDFSCapturing(String[][] board, int column, int row, String colour){
//        if (row < 0 || row >= board.length || column < 0 || column >= board[0].length) {
//            return 0;
//        }
//
//        if (board[row][column] == null || !board[row][column].equals(colour)) {
//            return 0;
//        }
//    }
//    private int intitializeCapturing(int column, int row, String colour){
//         return performDFSCapturing(this.board, column + 1, row, colour) +
//                 performDFSCapturing(this.board, column, row - 1, colour) +
//                 performDFSCapturing(this.board, column - 1, row, colour) +
//                 performDFSCapturing(this.board, column, row + 1, colour);
//
//    }



    public void makeMove(int column, int row, Player player){
        if(isMoveAllowed(column, row)){
            //TO DO
            // copy the board
            if(player == Player.BLACK){
                board.modifyBoard(column, row, Constants.BLACK);
            } else {
                board.modifyBoard(column, row, Constants.WHITE);
            }
        }
    }


    public boolean isMoveAllowed(int column, int row){
        if (!Objects.equals(board.getCellContent(column, row), Constants.EMPTY.value())) {
            System.out.println("cannot make a move");
            return false;
        } else {
            return true;
        }
    }
}
