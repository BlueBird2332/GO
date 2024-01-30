package org.example.gameEngine;

import org.example.models.CellContents;
import org.example.models.Constants;
import org.example.models.Stone;

import java.util.Arrays;
import java.util.Objects;

public class Board implements BoardInterface {

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
    public String[][] copyBoard(){
        String[][] copy = new String[rowSize()][colSize()];
        for(int i = 0; i < rowSize(); i++) {
            for (int j = 0; j < colSize(); j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }
    public int getSize() {
        return this.size;
    }
    public String getCellContent(int row, int column){
        if(row >= 0 && column >= 0 && row < size && column < size){
            return board[row][column];
        }
        else{
            return "";
        }
    }

    public void modifyBoard(int row, int column, Constants constant){
        this.board[row][column] = constant.value();
    }
    public void modifyBoard(int row, int column, String constant){
        this.board[row][column] = constant;
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

    public int colSize(){
        return this.board[0].length;
    }

    public int rowSize(){
        return this.board.length;
    }

    public boolean compareBoards(Board board){
        //printBoard();
        //board.printBoard();
        for(int i = 0; i < board.rowSize(); i++){
            for(int j = 0; j< board.colSize(); j++){
                if(!Objects.equals(this.getCellContent(i, j), board.getCellContent(i, j))){
                    return false;
                }
            }
        }
        return true;
    }
    public static Board getEmptyBoard(int size){
        String[][] board = new String[size][size];
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = CellContents.EMPTY.value();
            }
        }
        return new Board(board);
    }

    public void addStone(Stone stone){
        this.board[stone.row()][stone.column()] = stone.contents();
    }

    public Board deepCopy(){
        return new Board(this.copyBoard());
    }
}