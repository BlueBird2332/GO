package org.example;

public class Board {

    private char board[][];
    private int size;
    public void init(int size) {
        board = new char[size][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public void updateBoard (int row, int column, char token) {
        if (row<size && column<size) {
            board[row][column] = token;
        }
    }

    public void show () {
        for(int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                System.out.print("'" + board[i][j] + "'  ");
            }
            System.out.print("\n");
        }
    }

}
