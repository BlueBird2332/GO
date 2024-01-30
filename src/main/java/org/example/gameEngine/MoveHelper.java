package org.example.gameEngine;


import org.example.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.models.Stone.getOpponentColour;

public class MoveHelper {
    public static List<Stone> getNeighbours(int row, int column, Board b){
        List<Stone> neighbours = new ArrayList<>();
        String[][] board = b.getBoard();
        //Down
        if(row < board.length - 1){
            var cellContent = board[row + 1][column];
            neighbours.add(new Stone(row + 1, column, cellContent));
        }
        //Up
        if(row > 0){
            var cellContent = board[row - 1][column];
            neighbours.add(new Stone(row - 1, column, cellContent));
        }
        //Right
        if(column < board.length - 1){
            var cellContent = board[row][column + 1];
            neighbours.add(new Stone(row, column + 1, cellContent));
        }
        //Left
        if(column > 0){
            var cellContent = board[row][column - 1];
            neighbours.add(new Stone(row, column - 1, cellContent));
        }
        //System.out.println(neighbours);
        return neighbours;
    }

    public static boolean isMoveAllowed(Move move, Board board){
        return  isMoveWithinBounds(move, board) &&
                !isMoveSuicide(move, board)&&
                !isMoveAgainstKo(move, board) &&
                !isOccupied(move.row(), move.column(), board);
    }

    public static List<Move> getAvailableMoves(Board board, Player player){
        List<Move> moves = new ArrayList<>();
        for(int i = 0; i < board.rowSize(); i++){
            for(int j = 0; j < board.colSize(); j++){
                if(isMoveAllowed(new Move(i, j, player), board)){
                    moves.add(new Move(i, j, player));
                }
            }
        }
        return moves;
    }

    private static boolean isMoveAgainstKo(Move move, Board board){
        Stone stone = new Stone(move.row(), move.column(), move.player().value());
        var backup = board.deepCopy();
        backup.addStone(stone);
        return canCapture(stone, backup) && canBeCaptured(stone, backup) &&!canCaptureMany(stone, backup);
    }
    private static boolean canCapture(Stone stone, Board board){
        for(Stone neighbour : MoveHelper.getNeighbours(stone.row(), stone.column(), board)){
            if(neighbour.contents().equals(getOpponentColour(stone.contents()))){
                var group = Group.getGroup(neighbour, board);
                if(Group.groupBreathCount(new Group(group), board) == 0){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canCaptureMany(Stone stone, Board board){
        for(Stone neighbour : MoveHelper.getNeighbours(stone.row(), stone.column(), board)){
            if(neighbour.contents().equals(getOpponentColour(stone.contents()))){
                var group = Group.getGroup(neighbour, board);
                if((Group.groupBreathCount(new Group(group), board) == 0) && group.size() > 1){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canBeCaptured(Stone stone, Board board){
        var group = Group.getGroup(stone, board);
        return Group.groupBreathCount(new Group(group), board) <= 1;
    }

    private static boolean isMoveSuicide(Move move, Board board) {
        Stone stone = new Stone(move.row(), move.column(), move.player().value());
        var backup = board.deepCopy();
        backup.addStone(stone);
        boolean hasZeroBreaths = Group.groupBreathCount(new Group(Group.getGroup(stone, backup)), board) == 0;
        return !canCapture(stone, backup) && hasZeroBreaths;
    }


    private static boolean isMoveWithinBounds(Move move, Board board){
        return move.row() >= 0 && move.row() < board.rowSize() && move.column() >=0 && move.column() < board.colSize();
    }

    private static boolean isOccupied(int row, int column, Board board){
        return !board.getCellContent(row, column).equals(CellContents.EMPTY.value());
    }
}
