package org.gameEngine;

import org.Board.Board;
import org.gameEngine.CaptureHelper;
import org.junit.jupiter.api.Test;
import org.models.CellContents;
import org.models.Stone;

import static org.junit.jupiter.api.Assertions.*;

class CaptureHelperTest {

    @Test
    void testPerformCapturingSingle() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        Stone stone = new Stone(0,4, CellContents.WHITE.value());
        Board expectedResult = createBoardAfterCaptureSingle();

        //Act
        CaptureHelper.performCapturing(stone, oldBoard);

        //Assert
        assertTrue(expectedResult.compareBoards(oldBoard));
    }

    @Test
    void testPerformCapturingMultiple() {
        //Arrange
        Board oldBoard = createTestBoardMultiple();
        Stone stone = new Stone(4,4, CellContents.BLACK.value());
        Board expectedResult = createBoardAfterCaptureMultiple();

        //Act
        CaptureHelper.performCapturing(stone, oldBoard);

        //Assert
        oldBoard.printBoard();
        assertTrue(expectedResult.compareBoards(oldBoard));
    }

    @Test
    void testPerformCapturingFailure() {
        //Arrange
        Board oldBoard = createTestBoardFailure();
        Stone stone = new Stone(2,5, CellContents.BLACK.value());
        Board expectedResult = createTestBoardFailure();

        //Act
        CaptureHelper.performCapturing(stone, oldBoard);

        //Assert
        assertTrue(expectedResult.compareBoards(oldBoard));
    }
    @Test
    void testGetEnclosedAreaOnlyBlack() {
        //Arrange
        Board board = createTestBoardOnlyBlack();
        int expectedResult = 342;

        //Act
        int result = CaptureHelper.getEnclosedArea("B", board);


        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetEnclosedAreaBoth_Black() {
        // Arrange
        Board board = createTestBoardBoth();
        int expectedResult = 171;

        // Act
        int result = CaptureHelper.getEnclosedArea("B", board);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetEnclosedAreaBoth_White() {
        // Arrange
        Board board = createTestBoardBoth();
        int expectedResult = 153;

        // Act
        int result = CaptureHelper.getEnclosedArea("W", board);

        //Assert
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetEnclosedAreaNone() {
        // Arrange
        Board board = createTestBoardBoth();
        board.modifyBoard(3, 3, "W");

        int expectedResult = 0;

        // Act
        int result = CaptureHelper.getEnclosedArea("B", board);

        //Assert
        assertEquals(expectedResult, result);
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

    private Board createBoardAfterCaptureSingle(){

        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(0,4, CellContents.BLACK.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }

    private Board createTestBoardMultiple() {

        Board board = new Board(19);
        board.modifyBoard(0,4, CellContents.BLACK.value());
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.WHITE.value());
        board.modifyBoard(3,3, CellContents.BLACK.value());
        board.modifyBoard(3,5, CellContents.BLACK.value());
        board.modifyBoard(4,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(3,4, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();

        return board;
    }

    private Board createBoardAfterCaptureMultiple(){

        Board board = new Board(19);
        board.modifyBoard(0,4, CellContents.BLACK.value());
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(3,3, CellContents.BLACK.value());
        board.modifyBoard(3,5, CellContents.BLACK.value());
        board.modifyBoard(4,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
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

    private Board createTestBoardBoth() {
        Board board = new Board(19);
        for(int i = 0; i < 19; i++){
            board.modifyBoard(i,i, CellContents.BLACK.value());
        }
        for(int i = 0; i < 18; i++){
            board.modifyBoard(i + 1,i, CellContents.WHITE.value());
        }
        board.printBoard();
        return board;
    }
}
