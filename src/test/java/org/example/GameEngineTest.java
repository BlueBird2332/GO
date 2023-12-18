package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class GameEngineTest {


    private void fillBoard(Board board){
        board.modifyBoard(1,3, CellContents.BLACK);
        board.modifyBoard(1,5, CellContents.BLACK);
        board.modifyBoard(2,4, CellContents.BLACK);
        board.modifyBoard(0,4, CellContents.BLACK);
        board.modifyBoard(1,4, CellContents.WHITE);
        board.modifyBoard(0,3, CellContents.WHITE);
        board.modifyBoard(0,5, CellContents.WHITE);
        board.modifyBoard(1,10, CellContents.WHITE);
        //board.printBoard();
    }

}
