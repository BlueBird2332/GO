package org.gameEngine;

import org.Board.Board;
import org.gameEngine.CaptureHelper;
import org.junit.jupiter.api.Test;
import org.models.*;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    @Test
    void testMove() {
        //Arrange
        GameEngine ge = new GameEngine(19);
        Move move = new Move(0 ,4, Player.BLACK);

        Board expected = new Board(19);
        expected.modifyBoard(0, 4, Player.BLACK.value());


        //Act
        ge.makeMove(move);
        GameState state = ge.getCurrentState();

        //Assert
        assertTrue(state.board().compareBoards(expected));
    }

    @Test
    void testMoveSequence() {
        //Arrange
        GameEngine ge = new GameEngine(19);
        Move move = new Move(0 ,4, Player.BLACK);
        Move move1 = new Move(2 ,6, Player.WHITE);
        Move move2 = new Move(4 ,8, Player.BLACK);

        Board expected = new Board(19);
        expected.modifyBoard(0, 4, Player.BLACK.value());
        expected.modifyBoard(2, 6, Player.WHITE.value());
        expected.modifyBoard(4,8, Player.BLACK.value());


        //Act
        ge.makeMove(move);
        ge.makeMove(move1);
        ge.makeMove(move2);
        GameState state = ge.getCurrentState();

        //Assert
        assertTrue(state.board().compareBoards(expected));
    }

    @Test
    void testCapturing() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        GameEngine ge = new GameEngine(19);
        ge.setBoard(oldBoard);
        Move move = new Move(0 ,4, Player.BLACK);
        var expected = getBoardAfterCaptureSingle();

        //Act
        ge.makeMove(move);
        GameState state = ge.getCurrentState();

        //Assert
        assertTrue(state.board().compareBoards(expected));
    }


    @Test
    void testUpdatingGameState() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        GameEngine ge = new GameEngine(19);
        ge.setBoard(oldBoard);
        Move move = new Move(0 ,4, Player.BLACK);
        var expected = getBoardAfterCaptureSingle();
        var expectedWhiteCaptured = 1;
        var expectedBlackCaptured = 0;
        var expectedNextPlayer = Player.WHITE;
        var expectedGameState = new GameState(expected, expectedWhiteCaptured, expectedBlackCaptured, expectedNextPlayer);

        //Act
        ge.makeMove(move);
        GameState state = ge.getCurrentState();
        //state.printCurrentState();

        //Assert
        assertTrue(state.board().compareBoards(expectedGameState.board()));
        assertEquals(state.whitesCaptured(), expectedWhiteCaptured);
        assertEquals(state.blacksCaptured(), expectedBlackCaptured);
        assertEquals(state.nextPlayer(), expectedNextPlayer);
    }

    @Test
    void testReloadingFromState() {
        //Arrange
        Board oldBoard = createTestBoardSingle();
        GameEngine ge = new GameEngine(19);
        ge.setBoard(oldBoard);
        GameState memento = ge.getCurrentState();
        Move move = new Move(0 ,4, Player.BLACK);
        var expected = createTestBoardSingle();
        //expected.printBoard();
        var expectedWhiteCaptured = 0;
        var expectedBlackCaptured = 0;
        var expectedNextPlayer = Player.BLACK;
        var expectedGameState = new GameState(expected, expectedWhiteCaptured, expectedBlackCaptured, expectedNextPlayer);

        //Act
        ge.makeMove(move);
        ge.setState(memento);
        GameState state = ge.getCurrentState();
        //state.board().printBoard();
        //state.printCurrentState();

        //Assert
        assertTrue(state.board().compareBoards(expectedGameState.board()));
        assertEquals(state.whitesCaptured(), expectedWhiteCaptured);
        assertEquals(state.blacksCaptured(), expectedBlackCaptured);
        assertEquals(state.nextPlayer(), expectedNextPlayer);
    }


    private Board createTestBoardSingle() {


        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        //board.modifyBoard(0,4, CellContents.BLACK.value());
        //board.modifyBoard(0,5, CellContents.WHITE.value());
        //board.printBoard();
        return board;
    }

    private Board getBoardAfterCaptureSingle(){

        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(0,4, CellContents.BLACK.value());
        //board.printBoard();
        return board;
    }
}


