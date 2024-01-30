package org.example.gameEngine;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.example.models.CellContents;
import org.example.models.Constants;
import org.example.models.Stone;

public class BoardGUI implements BoardInterface{

    private Board board;
    private boolean moveEnable=true;
    private int rowSelected=-3;
    private  int columnSelected=-3;
    private final Object lock;


    public BoardGUI(int size, Object lock){
        board = new Board(size);
        this.lock=lock;
    }
    public BoardGUI(String[][] board) {
        this.board = new Board(board);
        lock = new Object();
    }
    public BoardGUI(Board board){
        this.board = board;
        moveEnable=false;
        lock=new Object();
    }


    public String[][] getBoard() {
        return this.board.getBoard();
    }
    public Board getBoardBoard() {
        return this.board;
    }
    public int getSize() {
        return this.board.getSize();
    }
    public String getCellContent(int row, int column){
        return this.board.getCellContent(row, column);
    }
    public void modifyBoard(int row, int column, Constants constant){
        this.board.modifyBoard(row,column, constant);
    }
    public void printBoard(){
        this.board.printBoard();
    }

    public int colSize(){
        return this.board.colSize();
    }

    public int rowSize(){
        return this.board.rowSize();
    }
    public void modifyBoard(int row, int column, String constant){
        this.board.modifyBoard(row, column, constant);
    }
    public void modifyBoard(Board newBoard){
        this.board = newBoard;
    }
    public boolean checkMoveEnable(){
        return moveEnable;
    }
    public void okToMove(){
        moveEnable = true;
    }
    public void stopMoving(){
        synchronized (lock) {
            moveEnable = false;
            lock.notify();  // Budzi jeden wątek, który czeka na notify()
        }
    }

    public void setRowSelected(int rowSelected){
        this.rowSelected = rowSelected;
    }
    public int getRowSelected(){
        return rowSelected;
    }
    public void setColumnSelected(int columnSelected){
        this.columnSelected = columnSelected;
    }
    public int getColumnSelected(){
        return columnSelected;
    }
    public synchronized void surrender(){
        rowSelected = -2;
        columnSelected = -2;
        stopMoving();


    }
    public synchronized void pass(){
        rowSelected = -1;
        columnSelected = -1;
        stopMoving();


    }
    public void addStone(Stone stone){
        this.board.addStone(stone);
    }


    public Pane showBoard(double windowWidth, double windowHeight) {
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.SADDLEBROWN, CornerRadii.EMPTY, Insets.EMPTY)));
        windowHeight-=40;

        int boardSize = this.getSize();
        double tileSize = Math.min(windowWidth, windowHeight)/boardSize;
        double widthOffset = (windowWidth - tileSize*boardSize)/2.0;
        double heightOffset = (windowHeight - tileSize*boardSize)/2.0;
        for (int i = 0; i < boardSize; i++) {
            Line verticalLine = new Line(i * tileSize + tileSize/2 + widthOffset, 0 + heightOffset, i * tileSize + tileSize/2 + widthOffset, boardSize * tileSize + heightOffset);
            Line horizontalLine = new Line(0 + widthOffset, i * tileSize + tileSize/2 + heightOffset, boardSize * tileSize + widthOffset, i * tileSize + tileSize/2  + heightOffset);

            verticalLine.setStroke(Color.BLACK);
            horizontalLine.setStroke(Color.BLACK);
            verticalLine.setStrokeWidth(4.0);
            horizontalLine.setStrokeWidth(4.0);

            root.getChildren().addAll(verticalLine, horizontalLine);
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {


                final int row = i;
                final int col = j;

                Circle stone = new Circle(tileSize / 4);
                stone.setCenterX(j * tileSize + tileSize / 2 + widthOffset);
                stone.setCenterY(i * tileSize + tileSize / 2 + heightOffset);
                if(board.getCellContent(row, col).equals(Constants.EMPTY.value())){
                    stone.setFill(Color.TRANSPARENT);
                }
                else if(board.getCellContent(row, col).equals(CellContents.VISITED.value())){
                    stone.setFill(Color.TRANSPARENT);
                }
                else if(board.getCellContent(row, col).equals(Constants.BLACK.value())){
                    stone.setFill(Color.BLACK);
                }
                else if(board.getCellContent(row, col).equals(Constants.WHITE.value())){
                    stone.setFill(Color.WHITE);
                }

                stone.setOnMouseClicked(event -> {
                    if(checkMoveEnable()){
                        chooseStone(row, col);
                    }

                });

                root.getChildren().add(stone);
            }
        }

        return root;

    }
    private synchronized void chooseStone(int row, int col){
        rowSelected = row;
        columnSelected = col;
        stopMoving();
        System.out.println("WYBRANO "+ row + " " + col);
        System.out.println("row "+rowSelected + " solumn" + columnSelected + " enable" + moveEnable);

    }
}