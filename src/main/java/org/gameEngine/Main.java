package org.gameEngine;

import org.Board.Board;
import org.Bot.Bot;
import org.models.CellContents;
import org.models.Move;

import static org.models.Player.BLACK;
import static org.models.Player.WHITE;

public class Main {
    public static void main(String[] args) {
        //testKo();
        //testNormal();
        //botTest();
        GameEngine ge = new GameEngine(19);
        ge.makeMove(new Move(0,2, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(0,4, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(1,0, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(1,3, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(2,2, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(3,1, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(4,0, BLACK));
        ge.printBoard();
        ge.makeMove(new Move(1,1, WHITE));
        ge.printBoard();
        ge.makeMove(new Move(1,2, WHITE));
        ge.printBoard();
        ge.makeMove(new Move(2,1, WHITE));
        ge.printBoard();
        ge.makeMove(new Move(2,0, WHITE));
        ge.printBoard();
        ge.makeMove(new Move(3,0, WHITE));
        ge.printBoard();

        ge.makeMove(new Move(0,1, WHITE));
        ge.printBoard();
        ge.makeMove(new Move(0,0, BLACK));
        ge.printBoard();



//        Board oldBoard = createTestBoardSingle();
//        ge.board = oldBoard;
//        ge.printBoard();
//        ge.makeMove(new Move(0,9, BLACK));
//        ge.printBoard();



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
        //board.printBoard();
        //board.modifyBoard(0,4, CellContents.BLACK);
        //board.printBoard();

    }

    private static void fillKoBoard(Board board){
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());

        //board.modifyBoard(2,4, CellContents.WHITE);
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,3, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        //board.modifyBoard(1,10, CellContents.WHITE.value());
        //board.printBoard();
        //board.modifyBoard(0,4, CellContents.BLACK);
        //board.printBoard();
    }

    public static void testKo(){
        GameEngine gameEngine = new GameEngine(11);
        //System.out.println(Arrays.deepToString(gameEngine.getBoard()));
        //gameEngine.printBoard();
        fillKoBoard(gameEngine.board);
        gameEngine.printBoard();
        gameEngine.makeMove(new Move(0,4, BLACK));
        gameEngine.printBoard();
        gameEngine.makeMove(new Move(1,4, WHITE));
        gameEngine.printBoard();
        gameEngine.makeMove(new Move(0,4, BLACK));
        gameEngine.printBoard();
    }

    public static void testNormal(){
        GameEngine gameEngine = new GameEngine(11);
        //System.out.println(Arrays.deepToString(gameEngine.getBoard()));
        //gameEngine.printBoard();
        fillBoard(gameEngine.board);
        gameEngine.printBoard();
        gameEngine.makeMove(new Move(0,9, BLACK));
        gameEngine.printBoard();
        gameEngine.makeMove(new Move(7,4, WHITE));
        gameEngine.printBoard();
        gameEngine.printBoard();
    }

    public static void botTest(){
        GameEngine gameEngine = new GameEngine(9);
        Bot bot = new Bot(gameEngine);
        //fillBoard(gameEngine.board);
        gameEngine.makeMove(new Move(1,4, BLACK));
        gameEngine.makeMove(new Move(1,2, BLACK));
        gameEngine.makeMove(new Move(2,3, BLACK));
        gameEngine.makeMove(new Move(1,3, WHITE));
        gameEngine.makeMove(new Move(0,4, BLACK));
        gameEngine.makeMove(new Move(0,4, BLACK));


        gameEngine.printBoard();
        gameEngine.makeMove(bot.findBestMove(1, BLACK));
        gameEngine.printBoard();

    }

    private static Board createTestBoardSingle() {


        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }

}