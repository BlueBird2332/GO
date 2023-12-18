package org.example;

import java.util.Arrays;

public class Board {

    private String[][] board ;
    public String[][] getBoard() {
        return board;
    }
    public Board(int size){
        // To do implement size
        this.board = new String[size][size];
        for (String[] strings : board) {
            Arrays.fill(strings, Constants.EMPTY.value());
        }
    }
    public String getCellContent(int column, int row){
        return board[column][row];
    }

    public void modifyBoard(int column, int row, Constants constant){
        this.board[column][row] = constant.value();
    }
    public void printBoard() {
        for (String[] strings : board) {
            for (String string : strings) {
                //
                if (string.equals(Constants.WHITE.value())){
                    System.out.printf("\u001B[32m%5s\u001B[0m", string);
                } else if (string.equals(Constants.BLACK.value())){
                    System.out.printf("\u001B[31m%5s\u001B[0m", string);
                } else {
                    System.out.printf("%5s", string);
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
