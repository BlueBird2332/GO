package org.gameEngine;

import org.Board.Board;
import org.models.CaptureResult;
import org.models.CellContents;
import org.models.Group;
import org.models.Stone;

import java.util.Arrays;
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
                    var temp = Group.getGroup(new Stone(i, j, CellContents.EMPTY.value()), board);
                    boolean foundEnemy = false;
                    for (Stone stone: temp) {
                        copyBoard.modifyBoard(stone.row(), stone.column(), VISITED.value());
                        if(MoveHelper.getNeighbours(stone.row(), stone.column(), copyBoard).stream().anyMatch(neighbour -> Objects.equals(neighbour.contents(), getOpponentColour(colour)))) {
                            foundEnemy = true;
                        }
                    }

                    if(!foundEnemy){
                        totalArea += temp.size();
                    }
                }
            }
        }
        return totalArea;

    }

    public static CaptureResult performCapturing(Stone stone, Board oldBoard){
        var board = initializeCapturing(stone, oldBoard.deepCopy());
        CaptureResult results = null;
        if(board != null){
            results = mergeBoards(oldBoard, board);
        }
        return results;
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
