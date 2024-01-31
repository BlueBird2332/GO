package org.example.bot;

import org.example.gameEngine.Board;
import org.example.gameEngine.CaptureHelper;
import org.example.gameEngine.GameEngine;
import org.junit.jupiter.api.Test;
import org.example.models.*;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {

    @Test
    void testFindMoveBasic() {
        //Arrange
        GameEngine ge = new GameEngine(19);
        Bot bot = new Bot(ge);


        Board expected = new Board(19);
        expected.modifyBoard(0, 0, Player.BLACK.value());


        //Act
        Move move = bot.findBestMove(1, Player.BLACK);
        ge.makeMove(move);

        //Assert
        assertTrue(ge.board.compareBoards(expected));
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
    void testResourcesReloadingFromState() {
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



