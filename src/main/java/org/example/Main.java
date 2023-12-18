package org.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        //System.out.println(Arrays.deepToString(gameEngine.getBoard()));
        gameEngine.printBoard();
        gameEngine.makeMove(1, 3, Player.BLACK);
        gameEngine.printBoard();
        gameEngine.makeMove(1, 2, Player.WHITE);
        gameEngine.printBoard();
        gameEngine.makeMove(1,2, Player.BLACK);
    }
}