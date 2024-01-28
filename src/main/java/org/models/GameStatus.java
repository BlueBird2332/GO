package org.models;

import jakarta.persistence.*;



@Entity
@Table(name = "Moves")
public class GameStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String board;

    @Column(nullable = false)
    private String nextMove;

    @Column
    private int blacksCaptured;

    @Column
    private int whitesCaptured;

    // Constructors, getters, and setters

    public GameStatus() {
    }

    public GameStatus(String board, String nextMove, int blacksCaptured, int whitesCaptured) {
        this.board = board;
        this.nextMove = nextMove;
        this.blacksCaptured = blacksCaptured;
        this.whitesCaptured = whitesCaptured;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getNextMove() {
        return nextMove;
    }

    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    public int getBlacksCaptured() {
        return blacksCaptured;
    }

    public void setBlacksCaptured(int blacksCaptured) {
        this.blacksCaptured = blacksCaptured;
    }

    public int getWhitesCaptured() {
        return whitesCaptured;
    }

    public void setWhitesCaptured(int whitesCaptured) {
        this.whitesCaptured = whitesCaptured;
    }
}