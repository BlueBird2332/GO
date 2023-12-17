package org.example;

import java.io.*;
import java.net.*;

/**
 *
 * @author aid
 */
public class Session implements Runnable {

    //declaring constants

    public static int PLAYER1_BLACK = 1;
    public static int PLAYER2_WHITE = 2;
    public static int DRAW = 3;
    public static int CONTINUE = 4;

    //sockets
    private Socket firstPlayer;
    private Socket secondPlayer;

    //Game
    //TODO: zintegorawac z GameEngine
    private GameEngine gameEngine;

    public Session(Socket firstPlayer, Socket secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        //TODO: jak GameEngine przygotowuje gre?
        gameEngine.prepareGame();
    }

    @Override
    public void run() {
        try {
            DataInputStream fromPlayer1 = new DataInputStream(firstPlayer.getInputStream());
            DataOutputStream toPlayer1 = new DataOutputStream(firstPlayer.getOutputStream());
            DataInputStream fromPlayer2 = new DataInputStream(secondPlayer.getInputStream());
            DataOutputStream toPlayer2 = new DataOutputStream(secondPlayer.getOutputStream());

            //notify player1 that player2 has joined
            toPlayer1.writeInt(1);

            //start the game
            while (true) {
                boolean guardian;
                int row, column;
                do {
                    //pas = (-1,-1)
                    //surrender = (-2, -2)
                    row = fromPlayer1.readInt();
                    column = fromPlayer1.readInt();
                    //TODO: czy makeMove zwraca boola?
                    guardian = GameEngine.makeMove();

                    if (!guardian) {
                        //illegal move
                        toPlayer1.writeInt(0);
                    }
                    else {
                        //legal move
                        toPlayer1.writeInt(1);
                    }
                } while (!guardian);

                //check if game is finished:
                //TODO:
                // - checking if game is finished
                // - getting winner
                if (gameEngine.isFinished()) {
                    if(gameEngine.getWinner() == PLAYER1_BLACK) {
                        //Black win - white passed, black passed, after counting points black win
                        //Inform players
                        toPlayer1.writeInt(PLAYER1_BLACK);
                        toPlayer2.writeInt(PLAYER1_BLACK);
                        //TODO: niepotrzebne wysylanie passowania?
                        sendMove(toPlayer2, row, column);
                        break;
                    }
                    if(gameEngine.getWinner() == PLAYER2_WHITE) {
                        //White win - white passed, black passed, after counting points white win
                        //Inform players
                        toPlayer1.writeInt(PLAYER2_WHITE);
                        toPlayer2.writeInt(PLAYER2_WHITE);
                        //TODO: niepotrzebne wysylanie passowania?
                        sendMove(toPlayer2, row, column);
                        break;
                    }
                    if(gameEngine.getWinner() == DRAW) {
                        //DRAW - white passed, black passed, after counting points noone has won
                        // Inform players
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        //TODO: niepotrzebne wysylanie passowania?
                        //sendMove(toPlayer2, row, column);
                        break;
                    }
                }
                else {
                    //game is not finished
                    toPlayer2.writeInt(CONTINUE);
                    sendMove(toPlayer2, row, column);
                }

                //Player2 moves - white
                do {
                    //pas = (-1,-1)
                    //surrender = (-2, -2);
                    row = fromPlayer2.readInt();
                    column = fromPlayer2.readInt();
                    //TODO: czy makeMove zwraca boola?
                    // - OR dodanie protokołów przesylania i w ogole kompletnie inaczej to robic
                    guardian = GameEngine.makeMove();

                    if (!guardian) {
                        //illegal move
                        toPlayer2.writeInt(0);
                    }
                    else {
                        //legal move
                        toPlayer2.writeInt(1);
                    }
                } while (!guardian);

                //check if game is finished:
                //TODO:
                // - checking if game is finished
                // - getting winner
                if (gameEngine.isFinished()) {
                    if(gameEngine.getWinner() == PLAYER1_BLACK) {
                        //Black win - white passed, black passed, after counting points black win
                        //Inform players
                        toPlayer1.writeInt(PLAYER1_BLACK);
                        toPlayer2.writeInt(PLAYER1_BLACK);
                        //TODO: niepotrzebne wysylanie passowania?
                        sendMove(toPlayer1, row, column);
                        break;
                    }
                    if(gameEngine.getWinner() == PLAYER2_WHITE) {
                        //White win - white passed, black passed, after counting points white win
                        //Inform players
                        toPlayer1.writeInt(PLAYER2_WHITE);
                        toPlayer2.writeInt(PLAYER2_WHITE);
                        //TODO: niepotrzebne wysylanie passowania?
                        sendMove(toPlayer1, row, column);
                        break;
                    }
                    if(gameEngine.getWinner() == DRAW) {
                        //DRAW - white passed, black passed, after counting points noone has won
                        // Inform players
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        //TODO: niepotrzebne wysylanie passowania?
                        sendMove(toPlayer1, row, column);
                        break;
                    }
                }
                else {
                    //game is not finished
                    toPlayer1.writeInt(CONTINUE);
                    sendMove(toPlayer1, row, column);
                }

            }
        } catch (IOException ex) {
            System.err.println("ex");
        }
    }

    private void sendMove(DataOutputStream out, int row, int column) throws IOException {
        out.writeInt(row);
        out.writeInt(column);
    }

}
