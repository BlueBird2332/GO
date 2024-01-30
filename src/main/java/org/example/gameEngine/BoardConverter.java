package org.example.gameEngine;

public class BoardConverter {
    public static String[][] decode(String encodedBoard){
        var splits = encodedBoard.split("\n");
        int size = splits.length;
        var board = new String[size][size];
        int j = 0;
        for(String s : splits){
            for(int i = 0; i < s.length(); i++){
                board[j][i] = Character.toString(s.charAt(i));
            }
            j++;
        }
        return board;
    }

    public static String encode(String[][] board){
        StringBuilder sb = new StringBuilder();
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col < board[0].length; col++){
                sb.append(board[row][col]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
