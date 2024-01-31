package org.example.gameEngine;

import org.example.gameEngine.Board;
import org.example.gameEngine.CaptureHelper;
import org.junit.jupiter.api.Test;
import org.example.models.CellContents;
import org.example.models.Move;
import org.example.models.Player;
import org.example.models.Stone;

import static org.junit.jupiter.api.Assertions.*;

class MoveHelperTest {

//    @Test
//    void testGetNeighbours() {
//        //Arrange
//        Board oldBoard = createTestBoardSingle();
//        Stone stone = new Stone(0,4, CellContents.WHITE.value());
//
//        //Act
//        CaptureHelper.performCapturing(stone, oldBoard);
//
//        //Assert
//       // assertTrue(expectedResult.compareBoards(oldBoard));
//    }

    @Test
    void testIsMoveAllowedAgainstKo() {
        //Arrange
        Board oldBoard = createBoardKo();
        Move move = new Move(0 ,4, Player.BLACK);

        //Act
        boolean result = MoveHelper.isMoveAllowed(move, oldBoard);

        //Assert
        assertFalse(result);
    }

    @Test
    void testIsMoveAllowedSuicide() {
        //Arrange
        Board oldBoard = createBoardSuicide();
        Move move = new Move(0 ,0, Player.WHITE);

        //Act
        boolean result = MoveHelper.isMoveAllowed(move, oldBoard);

        //Assert
        assertFalse(result);
    }

    @Test
    void testIsMoveAllowedSuicideException() {
        //Arrange
        Board oldBoard = createBoardSuicideException();
        Move move = new Move(0 ,0, Player.WHITE);

        //Act
        boolean result = MoveHelper.isMoveAllowed(move, oldBoard);

        //Assert
        assertTrue(result);
    }


    @Test
    void testIsMoveAllowedOutsideBorder() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        Move move = new Move(20 ,3, Player.BLACK);

        //Act
        boolean result = MoveHelper.isMoveAllowed(move, oldBoard);

        //Assert
        assertFalse(result);
    }

    @Test
    void testIsMoveAllowedOccupiedSpot() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        Move move = new Move(2 ,3, Player.BLACK);

        //Act
        boolean result = MoveHelper.isMoveAllowed(move, oldBoard);

        //Assert
        assertFalse(result);
    }

    private Board createTestBoardSingle() {


        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,4, CellContents.BLACK.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }

    private Board createBoardKo(){

        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,3, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }

    private Board createBoardSuicide() {

        Board board = new Board(19);
        board.modifyBoard(0,1, CellContents.BLACK.value());
        board.modifyBoard(1,1, CellContents.BLACK.value());
        board.modifyBoard(1,0, CellContents.BLACK.value());

        board.printBoard();

        return board;
    }

    private Board createBoardSuicideException(){

        Board board = new Board(19);
        board.modifyBoard(0,1, CellContents.BLACK.value());
        board.modifyBoard(1,1, CellContents.BLACK.value());
        board.modifyBoard(1,0, CellContents.BLACK.value());
        board.modifyBoard(0,2, CellContents.WHITE.value());
        board.modifyBoard(1,2, CellContents.WHITE.value());
        board.modifyBoard(2,2, CellContents.WHITE.value());
        board.modifyBoard(2,1, CellContents.WHITE.value());
        board.modifyBoard(2,0, CellContents.WHITE.value());

        board.printBoard();

        return board;
    }
    private Board createTestBoardFailure() {


        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,4, CellContents.BLACK.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }

    private Board createTestBoardOnlyBlack() {
        Board board = new Board(19);
        for(int i = 0; i < 19; i++){
            board.modifyBoard(i,i, CellContents.BLACK.value());
        }
        board.printBoard();
        return board;
    }
}

