package org.example.clientServer.GUI;

import org.example.clientServer.SessionInterface;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.game.GameEngine;
import org.example.game.Player;

import java.io.*;
import java.net.*;

/**
 *
 * @author aid
 */
public class SessionForGUI implements SessionInterface {

    //declaring constants
    //public static int PLAYER1_BLACK = 1;
    //public static int PLAYER2_WHITE = 2;
    //public static int DRAW = 3;
    //public static int CONTINUE = 4;

    //sockets
    private Socket firstPlayer;
    private Socket secondPlayer;

    ObjectInputStream fromPlayer1;
    ObjectOutputStream toPlayer1;
    ObjectInputStream fromPlayer2;
    ObjectOutputStream toPlayer2;

    //Game
    //TODO: zintegorawac z GameEngine
    private GameEngine gameEngine;
    private int passCounter = 0;

    public SessionForGUI(Socket firstPlayer, ObjectOutputStream toPlayer1, ObjectInputStream fromPlayer1, Socket secondPlayer, ObjectOutputStream toPlayer2, Integer boardSize) {
        this.firstPlayer = firstPlayer;
        this.toPlayer1 = toPlayer1;
        this.fromPlayer1 = fromPlayer1;
        this.secondPlayer = secondPlayer;
        this.toPlayer2 = toPlayer2;
        gameEngine = new GameEngine(boardSize);

    }

    @Override
    public void run() {
        try {

            //fromPlayer1 = new ObjectInputStream(firstPlayer.getInputStream());
            fromPlayer2 = new ObjectInputStream(secondPlayer.getInputStream());


            //notify player1 (Black) that player2 (White) has joined
            toPlayer1.writeObject(Player.WHITE);
            //System.out.println("Udało się powiadomic gościa");

            //start the game
            boolean gameFinished = false;
            while (true) {
                boolean guardian;

                //player1 moves
                do {
                    //pas = (-1,-1)
                    //surrender = (-2, -2)
                    guardian = false;
                    ClientToServerMessage move;
                    try {
                        move = (ClientToServerMessage) fromPlayer1.readObject();
                        toPlayer1.reset();
                        toPlayer2.reset();

                        if(move.type() == ClientToServerMessage.Type.SURRENDER) {
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                            guardian = false;
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), Player.WHITE));
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), Player.WHITE));
                            gameFinished = true;

                        }
                        else if(move.type() == ClientToServerMessage.Type.PASS) {
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                            guardian = false;
                            passCounter++;
                            if(passCounter >=2) {
                                //dTODO - what does that do?
                                Player winner = gameEngine.winner();
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), winner));
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), winner));
                                gameFinished = true;

                            }
                            else{
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.OTHER_PLAYER_PASSED, gameEngine.getBoard(), Player.BLACK));
                            }

                        }
                        else {
                            passCounter = 0;
                            if(move.type() == ClientToServerMessage.Type.MAKE_MOVE) {
                                if(gameEngine.isMoveAllowed(move.row(), move.column(), move.player())) {
                                    gameEngine.makeMove(move.row(), move.column(), move.player());
                                    //gameEngine.printBoard();
                                    gameEngine.getBoard().printBoard();
                                    toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                                    toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_MADE, gameEngine.getBoard(), Player.BLACK));
                                    guardian = false;
                                }
                                else {
                                    toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_FAILURE, null, null));
                                    guardian = true;
                                }
                            }
                        }
                    }
                    catch(ClassNotFoundException ex) {
                        System.err.println(ex);
                    }
                } while (guardian);

                //check if game is finished:
                if (gameFinished) {
                    break;
                }

                //Player2 moves - white
                do {
                    //pas = (-1,-1)
                    //surrender = (-2, -2);

                    guardian = false;
                    ClientToServerMessage move;
                    try {
                        move = (ClientToServerMessage) fromPlayer2.readObject();
                        toPlayer1.reset();
                        toPlayer2.reset();

                        if(move.type() == ClientToServerMessage.Type.SURRENDER) {
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                            guardian = false;
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), Player.BLACK));
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), Player.BLACK));
                            gameFinished = true;

                        }
                        else if(move.type() == ClientToServerMessage.Type.PASS) {
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                            guardian = false;
                            passCounter++;
                            if(passCounter >=2) {
                                Player winner = gameEngine.winner();
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), winner));
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED, gameEngine.getBoard(), winner));
                                gameFinished = true;

                            }
                            else{
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.OTHER_PLAYER_PASSED, gameEngine.getBoard(), Player.WHITE));
                            }

                        }
                        else {
                            passCounter = 0;
                            if(move.type() == ClientToServerMessage.Type.MAKE_MOVE) {
                                if(gameEngine.isMoveAllowed(move.row(), move.column(), move.player())) {
                                    gameEngine.makeMove(move.row(), move.column(), move.player());
                                    gameEngine.printBoard();
                                    toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL, gameEngine.getBoard(), null));
                                    toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_MADE, gameEngine.getBoard(), Player.WHITE));
                                    guardian = false;
                                }
                                else {
                                    toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_FAILURE, null, null));
                                    guardian = true;
                                }
                            }
                        }
                    }
                    catch(ClassNotFoundException ex) {
                        System.err.println(ex);
                    }
                } while (guardian);

                //check if game is finished:
                if (gameFinished) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}

