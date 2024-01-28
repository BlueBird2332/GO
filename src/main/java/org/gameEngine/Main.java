package org.gameEngine;

import org.models.CellContents;

import java.util.Arrays;

import static org.models.Player.BLACK;

public class Main {
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine(11);
        //System.out.println(Arrays.deepToString(gameEngine.getBoard()));
        //gameEngine.printBoard();
        fillKoBoard(gameEngine.board);
        gameEngine.makeMove(0,4, BLACK);
        //gameEngine.makeMove(1,4, WHITE);
        gameEngine.printBoard();
        //gameEngine.makeMove(0,4, BLACK);
        gameEngine.printBoard();
        System.out.println(gameEngine.getEnclosedArea(CellContents.BLACK.value()));
        var encoded = BoardConverter.encode(gameEngine.board.getBoard());
        System.out.println(encoded);
        var decoded = BoardConverter.decode(encoded);
        System.out.println(Arrays.deepToString(decoded));


    }

    private static void fillBoard(Board board){
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(3,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        //board.modifyBoard(1,3, CellContents.BLACK);

        board.modifyBoard(2,4, CellContents.WHITE.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        //board.modifyBoard(0,3, CellContents.WHITE.value());
        //board.modifyBoard(0,5, CellContents.WHITE.value());
        //board.modifyBoard(1,10, CellContents.WHITE.value());
        board.printBoard();
        //board.modifyBoard(0,4, CellContents.BLACK);
        board.printBoard();

    }

    private static void fillKoBoard(Board board){
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        //board.modifyBoard(1,3, CellContents.BLACK);

        //board.modifyBoard(2,4, CellContents.WHITE);
        board.modifyBoard(1,4, CellContents.WHITE.value());
        //board.modifyBoard(0,3, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        //board.modifyBoard(1,10, CellContents.WHITE.value());
        board.printBoard();
        //board.modifyBoard(0,4, CellContents.BLACK);
        //board.printBoard();
    }
}