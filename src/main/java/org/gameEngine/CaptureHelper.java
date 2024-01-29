package org.gameEngine;

import org.Board.Board;
import org.models.CaptureResult;
import org.models.CellContents;
import org.models.Stone;

import java.util.Objects;

import static org.gameEngine.MoveHelper.getNeighbours;
import static org.models.CellContents.VISITED;
import static org.models.Stone.getOpponentColour;
import static org.models.Stone.hasLiberties;

public class CaptureHelper {
    public static int getEnclosedArea(String colour, Board board){
        Board copyBoard = new Board(board.copyBoard());
        int totalArea = 0;

        for (int i = 0; i < copyBoard.rowSize(); i++) {
            for (int j = 0; j < copyBoard.colSize(); j++) {
                if (Objects.equals(copyBoard.getCellContent(i, j), CellContents.EMPTY.value())) {
                    var temp = new Board(copyBoard.copyBoard());
                    if(!markAreaDFS(temp, new Stone(i, j, CellContents.EMPTY.value()), getOpponentColour(colour))){
                        var cr = mergeBoards(copyBoard, temp);
                        totalArea += cr.count();
                    };
                }
            }
        }
        return totalArea;
    }

    private static boolean markAreaDFS(Board board, Stone stone, String opponnentsColour){
        // To DO
        board.modifyBoard(stone.row(), stone.column(), VISITED.value());
        boolean foundOpponentFlag = false;
        for(Stone neighbour : getNeighbours(stone.row(), stone.column(),  board)){
            if(neighbour.contents().equals(stone.contents())){
                //Another empty stone - continue search
                foundOpponentFlag = foundOpponentFlag || markAreaDFS(board, neighbour, opponnentsColour);
            }
            if(neighbour.contents().equals(opponnentsColour)){
                foundOpponentFlag = true;
            }
        }
        return foundOpponentFlag;
    }
    public static void performCapturing(Stone stone, Board oldBoard){
        var board = initializeCapturing(stone, oldBoard.deepCopy());
        if(board != null){
            var results = mergeBoards(oldBoard, board);
        }
    }

    private static Board initializeCapturing(Stone stone, Board copiedBoard){
        var neighbours = getNeighbours(stone.row(), stone.column(), copiedBoard);
        boolean hasNoBreaths = false;
        for(Stone n : neighbours) {
            boolean temp = searchDFSToCapture(n, copiedBoard);
            hasNoBreaths = hasNoBreaths || temp;
        }
        if(hasNoBreaths) {
            return copiedBoard;
        }
        return null;
    }

    private static boolean searchDFSToCapture(Stone stone, Board board){

        if (hasLiberties(getNeighbours(stone.row(), stone.column(), board))) {
            // capturing cannot be performed no point in carrying on
            return false;
        }
        board.modifyBoard(stone.row(), stone.column(),VISITED.value());
        boolean result = true;
        for(Stone neighbour : getNeighbours(stone.row(), stone.column(), board)) {
            if (Objects.equals(neighbour.contents(), stone.contents())) {
                //continue searching
                result = result && searchDFSToCapture(neighbour, board);
            }
        }
        return result;
    }

    private static CaptureResult mergeBoards(Board oldBoard, Board newBoard){

        int count = 0;
        String colour = "";
        if (oldBoard.rowSize() != newBoard.rowSize()
                || oldBoard.colSize() != newBoard.colSize()) {
            throw new IllegalArgumentException("Arrays' size do not match");
        } else {
            for(int i = 0; i < oldBoard.rowSize(); i++){
                for(int j = 0; j < oldBoard.colSize(); j++){
                    if(oldBoard.getCellContent(i, j).equals(newBoard.getCellContent(i ,j))){
                        continue;
                    } else {
                        count++;
                        colour = oldBoard.getCellContent(i, j);
                        oldBoard.modifyBoard(i, j, CellContents.EMPTY.value());
                    }
                }
            }
        }
        return new CaptureResult(count, colour);
    }
}
