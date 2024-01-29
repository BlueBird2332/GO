package org.gameEngine;

import org.models.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class GameEngine {
    private GameState currentState;
    public Board board;

    private int whiteCaptured = 0;
    private int blackCaptured = 0;

    private List<Board> koHistory;
    public GameEngine(int size){
        initializeBoard(size);
        currentState = new GameState(new Board(size), 0, 0, Player.BLACK, koHistory);
    };

    public void initializeBoard(int size) {
        this.board = new Board(size);
        koHistory = new ArrayList<>(List.of(new Board(board.colSize()), new Board(board.colSize())));
    }
    private boolean isOccupied(int row, int column){
        return !board.getCellContent(row, column).equals(CellContents.EMPTY.value());
    }

    private String[][] initializeCapturing(Stone stone, String[][] copiedBoard){
         var neighbours = getNeighbours(stone.row(), stone.column());
         //System.out.println(neighbours.size());
         boolean hasNoBreaths = false;
         int iteraition = 0;
         for(Stone n : neighbours){
             boolean temp = searchDFSToCapture(n, copiedBoard);
             hasNoBreaths = hasNoBreaths || temp;
             //System.out.println(iteration);
             iteraition++;
         }
         //System.out.println(hasNoBreaths);
         if(hasNoBreaths) {
             //System.out.println(Arrays.deepToString(copiedBoard));
             return copiedBoard;
         }
         return null;

    }
    private boolean searchDFSToCapture(Stone stone, String[][] board){

        if (hasLiberties(getNeighbours(stone.row(), stone.column(), board))) {
            // capturing cannot be performed no point in carrying on
            //System.out.println("Has Liberties at location " + stone.row() + "  " + stone.column() + "with value" + stone.contents());
            return false;
        }
        board[stone.row()][stone.column()] = CellContents.VISITED.value();
        boolean result = true;
        for(Stone neighbour : getNeighbours(stone.row(), stone.column(), board)) {
//            System.out.println(neighbour);
//            System.out.println(stone.contents());
//            System.out.println(board[stone.row()][stone.column()]);
            if (Objects.equals(neighbour.contents(), stone.contents())) {
                //continue searching
                //System.out.println("Searching at location " + stone.row() + "  " + stone.column() + "with value" + stone.contents());
                result = result && searchDFSToCapture(neighbour, board);
            }
        }
        return result;
    }

    private void performCapturing(Stone stone){
        var board = initializeCapturing(stone, this.board.copyBoard());
        if(board != null){
            mergeBoards(this.board, new Board(board));
        }
    }

    private List<Stone> getNeighbours(int row, int column){
        return getNeighbours(row, column, this.board.getBoard());
    }

    private List<Stone> getNeighbours(int row, int column, String[][] board){
        List<Stone> neighbours = new ArrayList<>();
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

    private boolean hasLiberties(List<Stone> neighbours){
        return neighbours.stream().anyMatch(stone -> Objects.equals(stone.contents(), CellContents.EMPTY.value()));
    }

    private CaptureResult mergeBoards(Board oldBoard, Board newBoard){
        return mergeBoards(oldBoard, newBoard, CellContents.EMPTY.value());
    }
    private CaptureResult mergeBoards(Board oldBoard, Board newBoard, String contentsToFillWith){

        System.out.println("Capturing performed merging boards");
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
                        oldBoard.modifyBoard(i, j, contentsToFillWith);
                    }
                }
            }
        }
        return new CaptureResult(count, colour);
    }

    private void updateResults(CaptureResult cr){
        if(Objects.equals(cr.colour(), CellContents.BLACK.value())){
            blackCaptured += cr.count();
        } else {
            whiteCaptured += cr.count();
        }
    }


    public void makeMove(Move move) {
        if (isMoveAllowed(move.row(), move.column(), move.player().value())) {
            board.modifyBoard(move.row(), move.column(), move.player().value());
            performCapturing(new Stone(move.row(), move.column(), Player.getOpponent(move.player()).value()));
            updateHistory();
            updateStatus(move);
        }
    }


    public boolean isMoveAllowed(int row, int column, String colour){
        return  isMoveWithinBounds(row, column) &&
                !isMoveSuicide(row, column, colour) &&
                !isMoveAgainstKo(row, column, colour) &&
                !isOccupied(row, column);
    }

    private boolean isMoveWithinBounds(int row, int column){
        return Objects.equals(board.getCellContent(row, column), CellContents.EMPTY.value());
    }

    public int getState(){
        int blacksPoints = getEnclosedArea(CellContents.BLACK.value()) + whiteCaptured;
        int whitesPoints  = getEnclosedArea(CellContents.WHITE.value()) + blackCaptured;
        return whitesPoints - blacksPoints;
    }


    public int getEnclosedArea(String colour){
        Board copyBoard = new Board(board.copyBoard());
        int totalArea = 0;

        for (int i = 0; i < copyBoard.rowSize(); i++) {
            for (int j = 0; j < copyBoard.colSize(); j++) {
                if (Objects.equals(copyBoard.getCellContent(i, j), CellContents.EMPTY.value())) {
                    var temp = new Board(copyBoard.copyBoard());
                    if(!markAreaDFS(temp, new Stone(i, j, CellContents.EMPTY.value()), getOpponentColour(colour))){
                        var cr = mergeBoards(copyBoard, temp, CellContents.VISITED.value());
                        totalArea += cr.count();
                    };
                }
            }
        }
        return totalArea;
    }

    private boolean markAreaDFS(Board board, Stone stone, String opponnentsColour){
        // To DO
        board.modifyBoard(stone.row(), stone.column(), CellContents.VISITED.value());
        boolean foundOpponentFlag = false;
        for(Stone neighbour : getNeighbours(stone.row(), stone.column(),  board.getBoard())){
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
//
    private boolean isMoveSuicide(int row, int column, String colour) {
        if(!isMoveWithinBounds(row, column)){
            return false;
        }
        var copyBoard = this.board.copyBoard();
        copyBoard[row][column] = colour;
        boolean canBeCaptured = searchDFSToCapture(new Stone(row, column, colour), copyBoard);

        copyBoard = this.board.copyBoard();
        copyBoard[row][column] = colour;
//
        var result = initializeCapturing(new Stone(row, column, getOpponentColour(colour)), copyBoard);
        boolean canCapture = result != null;
        return !canCapture && canBeCaptured;
    }
//
    private boolean isMoveAgainstKo(int row, int column, String colour){
        var copyBoard = board.copyBoard();
        copyBoard[row][column] = colour;
        for(Board b : koHistory){
            //b.printBoard();

            var temp = new Board(copyBoard);
            if(b.compareBoards(temp)){
                System.out.println("Move Against Ko");
                return true;
            }
        }
        System.out.println("-".repeat(20));
        return false;
    }
    private String getOpponentColour(String colour){
        if (colour.equals(CellContents.BLACK.value())){
            return CellContents.WHITE.value();
        }
        if (colour.equals(CellContents.WHITE.value())){
            return CellContents.BLACK.value();
        }
        throw new IllegalArgumentException("Invalid Colour");
    }

    public boolean hasBreaths(Group group){
        for(Stone stone : group.stones()){
            if (hasBreath(stone)){
                return true;
            }
        }
        return false;
    };

    public int groupBreathCount(Group group){
        int count = 0;
        for(Stone stone : group.stones()){
            count += breathCount(stone);
        }
        return count;
    }

    public int breathCount(Stone stone) {
        int count = 0;
        for(Stone neighbour : getNeighbours(stone.row(), stone.column())){
            if(neighbour.contents().equals(CellContents.EMPTY.value())){
                count += 1;
            }
        }
        return count;
    }

    public boolean hasBreath(Stone stone){
        return breathCount(stone) != 0;
    }

    private void updateHistory(){
        koHistory.set(1, koHistory.get(0));
        koHistory.set(0, new Board(this.board.copyBoard()));
    }
    public void printBoard(){
        this.board.printBoard();
    }

    public List<Move> getAvailableMoves(Board board, Player player){
        List<Move> moves = new ArrayList<>();
        var colour = player.value();
        for(int i = 0; i < board.rowSize(); i++){
            for(int j = 0; j < board.colSize(); j++){
                if(isMoveAllowed(i, j, colour)){
                    moves.add(new Move(i, j, player));
                }
            }
        }
        System.out.println(moves.toString());
        return moves;
    }

    private void updateStatus(Move move){
        currentState = new GameState(new Board(this.board.copyBoard()), whiteCaptured, blackCaptured,
                Player.getOpponent(move.player()), this.koHistory);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState currentState){
        this.currentState = GameState.getDeepCopy(currentState);
        updateLocalVariables(currentState);
    }
    private void updateLocalVariables(GameState state){
        this.board = state.board().deepCopy();
        this.whiteCaptured = state.whitesCaptured();
        this.blackCaptured = state.blacksCaptured();
        this.koHistory = state.koList();
    }
}

