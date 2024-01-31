package org.example.models;

import org.example.gameEngine.Board;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.models.Group.getGroup;

public class GroupTest {

    @Test
    void testGetGroup_singleStone() {
        // Arrange
        Board board = boardInitializer();
        Stone stone = new Stone(1, 4, "W");

        //Act
        List<Stone> group = getGroup(stone, board);

        // Assert
        assertEquals(1, group.size());
        assertTrue(group.contains(stone));
    }

    @Test
    void testGetGroup_connectedStones() {
        // Arrange
        Board board = boardInitializer();
        Stone stone = new Stone(1, 3, "B");

        // Act
        List<Stone> group = getGroup(stone, board);

        // Assert
        assertEquals(5, group.size());
        assertTrue(group.contains(stone));

    }

    @Test
    void testGetGroup_disconnectedStones() {
        // Arrange
        Board board = boardInitializer();
        Stone stone = new Stone(1, 3, "B");

        // Act
        List<Stone> group = getGroup(stone, board);

        // Assert
        // Add assertions based on your specific test scenario
        // For example, if stones are not connected, you might check group.size() == 1
    }
    private Board boardInitializer(){
        Board board = new Board(19);
        board.modifyBoard(2,3, CellContents.BLACK.value());
        board.modifyBoard(2,5, CellContents.BLACK.value());
        board.modifyBoard(2,4, CellContents.BLACK.value());
        board.modifyBoard(1,5, CellContents.BLACK.value());
        board.modifyBoard(1,3, CellContents.BLACK.value());
        board.modifyBoard(1,4, CellContents.WHITE.value());
        board.modifyBoard(0,3, CellContents.WHITE.value());
        board.modifyBoard(0,5, CellContents.WHITE.value());
        board.printBoard();
        return board;
    }
}
