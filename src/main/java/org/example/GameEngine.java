package org.example;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class GameEngine {
    public Board board ;
    public void printBoard(){
        this.board.printBoard();
    }
    private int whiteCaptured = 0;
    private int blackCaptured = 0;

    private List<Board> koHistory;
    public GameEngine(int size){
        initializeBoard(size);
    };

    public void initializeBoard(int size) {
        this.board = new Board(size);
        koHistory = new ArrayList<>(List.of(new Board(board.colSize()), new Board(board.colSize())));
    }


    private String[][] initializeCapturing(Stone stone, String[][] copiedBoard){
         var neighbours = getNeighbours(stone.row(), stone.column());
         //System.out.println(neighbours.size());
         boolean hasNoBreaths = false;
         int iteraition = 0;
         for(Stone n : neighbours){
             boolean temp = searchDFSToCapture(n, copiedBoard);
             hasNoBreaths = hasNoBreaths || temp;
             //System.out.println(iteraition);
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


    private void mergeBoards(Board oldBoard, Board newBoard){

        System.out.println("Capturing performed merging boards");

        if (oldBoard.rowSize() != newBoard.rowSize()
            || oldBoard.colSize() != newBoard.colSize()) {
            throw new IllegalArgumentException("Arrays' size do not match");
        }
        else {
            for(int i = 0; i < oldBoard.rowSize(); i++){
                for(int j = 0; j < oldBoard.colSize(); j++){
                    if(oldBoard.getCellContent(i, j).equals(newBoard.getCellContent(i ,j))){
                        continue;
                    } else {
                        if(Objects.equals(oldBoard.getCellContent(i, j), CellContents.BLACK.value())){
                            blackCaptured += 1;
                        } else {
                            whiteCaptured += 1;
                        }
                        oldBoard.modifyBoard(i, j, CellContents.EMPTY);
                    }
                }
            }
        }
    }





    public void makeMove(int row, int column, Player player){
        if(isMoveAllowed(row, column, player.value())){
            if(player == Player.BLACK){
                board.modifyBoard(row, column, CellContents.BLACK);
                updateHistory();
                performCapturing(new Stone(row, column, CellContents.WHITE.value()));
            } else {
                board.modifyBoard(row, column, CellContents.WHITE);
                updateHistory();
                performCapturing(new Stone(row, column, CellContents.BLACK.value()));
            }
        }
    }


    public boolean isMoveAllowed(int row, int column, String colour){
        return isMoveWithinBounds(row, column) && !isMoveSuicide(row, column, colour) && !isMoveAgainstKo(row, column, colour);
    }

    private boolean isMoveWithinBounds(int row, int column){
        if (!Objects.equals(board.getCellContent(row, column), CellContents.EMPTY.value())) {
            System.out.println("cannot make a move");
            return false;
        } else {
            return true;
        }
    }

    private void getState(){
        int blacksPoints = getEnclosedArea(CellContents.BLACK.value()) + whiteCaptured;
        int whitesPoints  = getEnclosedArea(CellContents.WHITE.value()) + blackCaptured;
    }


    private int getEnclosedArea(String colour){
        String[][] copyBoard = board.copyBoard();
        int totalArea = 0;

        for (int i = 0; i < copyBoard.length; i++) {
            for (int j = 0; j < copyBoard[0].length; j++) {
                if (!Objects.equals(copyBoard[i][j], CellContents.VISITED.value())) {
                     //markAreaDFS(copyBoard, new Stone(i, j, colour));
                }
            }
        }
        return 1;
    }

    private boolean markAreaDFS(String[][] board, Stone stone){
        return true;
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
//        System.out.println(result);
        boolean canCapture = result != null;
//        System.out.println("can capture = " + canCapture);
//        System.out.println("can be Captured = " + canBeCaptured);
        return !canCapture && canBeCaptured;
    }
//
    private boolean isMoveAgainstKo(int row, int column, String colour){
        var copyBoard = board.copyBoard();
        copyBoard[row][column] = colour;
        for(Board b : koHistory){
            b.printBoard();
            var temp = new Board(copyBoard);
            if(b.compareBoards(temp)){
                System.out.println("Move Against Ko");
                return true;
            }
        }
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
}

