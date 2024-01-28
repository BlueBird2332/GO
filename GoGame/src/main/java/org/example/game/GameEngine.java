package org.example.game;

import java.util.Objects;

public final class GameEngine {
    private Board board ;
    public void printBoard(){
        this.board.printBoard();
    }
    private int whiteCaptured = 0;
    private int blackCaptured = 0;

    public GameEngine(Integer boardSize){
        this.board = new Board(boardSize);
    }
    public GameEngine(){
        this.board = new Board(19);
    }

    public Board getBoard() {
        return this.board;
    }
    public String[][] getBoardContents() {
        String[][] result = this.board.getBoard();
        return result;
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



    public void makeMove(int row, int column, Player player){
        if(isMoveAllowed(row, column, player)){
            //TO DO
            // copy the board
            if(player == Player.BLACK){
                board.modifyBoard(row, column, Constants.BLACK);
            } else {
                board.modifyBoard(row, column, Constants.WHITE);
            }
        }
    }

    public boolean isMoveAllowed(int row, int column, Player player){
        if (column < 0 || column >= board.getSize() || row < 0 || row >= board.getSize()) {
            return false;
        }
        if (!Objects.equals(board.getCellContent(row, column), Constants.EMPTY.value())) {
            System.out.println("cannot make a move");
            return false;
        } else {
            return true;
        }
    }

    public Player winner(){
        //TODO: obliczanie winnerra
        //for now wieczny remis;
        return null;
    }
}