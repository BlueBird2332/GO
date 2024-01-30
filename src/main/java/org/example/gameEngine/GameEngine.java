package org.example.gameEngine;

import org.example.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.example.gameEngine.CaptureHelper.getEnclosedArea;
import static org.example.gameEngine.CaptureHelper.performCapturing;


public final class GameEngine {
    private GameState currentState;
    public Board board ;

    private int whiteCaptured = 0;
    private int blackCaptured = 0;


    public GameEngine(){
        this.board = new Board(19);
    }
    public GameEngine(int size){
        initializeBoard(size);
        currentState = new GameState(new Board(size), 0, 0, Player.BLACK);
    };

    public void initializeBoard(int size) {
        this.board = new Board(size);
    }

    private List<Stone> getNeighbours(int row, int column){
        return MoveHelper.getNeighbours(row, column, this.board);
    }

    private void updateResults(CaptureResult cr){
        if(Objects.equals(cr.colour(), CellContents.BLACK.value())){
            blackCaptured += cr.count();
        } else {
            whiteCaptured += cr.count();
        }
    }

    public void makeMove(Move move) {
        //System.out.println("Move made");
        if (MoveHelper.isMoveAllowed(move, this.board)) {
            //make Move
            board.modifyBoard(move.row(), move.column(), move.player().value());
            //printBoard();
            //System.out.println("Move made");
            //check for capturing
            performCapturing(new Stone(move.row(), move.column(), Player.getOpponent(move.player()).value()), this.board);
            //update results
            updateStatus(move);
        }
        else{
            //System.out.println("Can't make move");
        }
    }



    public int getState(){
        int blacksPoints = getEnclosedArea(CellContents.BLACK.value(), this.board.deepCopy()) - whiteCaptured;
        int whitesPoints  = getEnclosedArea(CellContents.WHITE.value(), this.board.deepCopy()) + blackCaptured;
        return whitesPoints - blacksPoints;
    }
    public void printBoard(){
        this.board.printBoard();
    }


    private void updateStatus(Move move){
        currentState = new GameState(new Board(this.board.copyBoard()), whiteCaptured, blackCaptured,
                Player.getOpponent(move.player()));
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
    }

    public Board getBoard() {
        return this.board;
    }
    public String[][] getBoardContents() {
        String[][] result = this.board.getBoard();
        return result;
    }

    //TODO: ogarnąć makemove wszędzie
    public void makeMove(int row, int column, Player player){
        Move move = new Move(row, column, player);
        makeMove(move);
    }

    //TODO: ogarnąć te zmiany wszędzie
    public boolean isMoveAllowed(int row, int column, Player player){
        Move move = new Move(row, column, player);
        return MoveHelper.isMoveAllowed(move, this.board);
    }

    public Player winner(){

        if(this.getState()>0){
            return Player.WHITE;
        }
        else if (this.getState()==0){
            //remis
            return null;
        }
        else {
            return Player.BLACK;
        }
    }
}