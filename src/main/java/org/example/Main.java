package org.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        //System.out.println(Arrays.deepToString(gameEngine.getBoard()));
        GameEngine.printBoard(gameEngine.getBoard());
        gameEngine.makeMove(1, 3);
        GameEngine.printBoard(gameEngine.getBoard());
        gameEngine.makeMove(1, 2);
        GameEngine.printBoard(gameEngine.getBoard());
        gameEngine.makeMove(1,2);
    }
}