package org.example.game;

import java.io.Serializable;
import java.util.Arrays;
public class Board implements Serializable {

    private String[][] board ;
    private int size;
    public Board(int size){
        this.size = size;
        this.board = new String[size][size];
        for (String[] strings : board) {
            Arrays.fill(strings, Constants.EMPTY.value());
        }
    }
    public Board (String[][] board) {
        this.board = board;
    }

    public String[][] getBoard() {
        return board;
    }
    public int getSize() {
        return this.size;
    }
    public String getCellContent(int row, int column){
        return board[row][column];
    }

    public void modifyBoard(int row, int column, Constants constant){
        this.board[row][column] = constant.value();
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