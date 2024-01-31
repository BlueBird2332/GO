package org.example.clientServer.GUI;

import org.example.clientServer.SessionInterface;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.gameEngine.Board;
import org.example.gameEngine.GameEngine;
import org.example.models.GameState;
import org.example.models.Player;
import org.example.sampleDatabase.DatabaseUtils;
import org.example.sampleDatabase.HibernateUtil;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 *
 * @author aid
 */
public class SessionForGUI implements SessionInterface {
//Mediator dla clientów
    //sockets
    private Socket firstPlayer;
    private Socket secondPlayer;

    ObjectInputStream fromPlayer1;
    ObjectOutputStream toPlayer1;
    ObjectInputStream fromPlayer2;
    ObjectOutputStream toPlayer2;

    private GameEngine gameEngine;
    private int passCounter = 0;
    private DatabaseUtils dbUtil;
    private long gameID;

    public SessionForGUI(Socket firstPlayer, ObjectOutputStream toPlayer1, ObjectInputStream fromPlayer1, Socket secondPlayer, ObjectOutputStream toPlayer2, Integer boardSize) {
        this.firstPlayer = firstPlayer;
        this.toPlayer1 = toPlayer1;
        this.fromPlayer1 = fromPlayer1;
        this.secondPlayer = secondPlayer;
        this.toPlayer2 = toPlayer2;
        gameEngine = new GameEngine(boardSize);
        dbUtil = new DatabaseUtils();

    }

    @Override
    public void run() {
        HibernateUtil.getSessionFactory();
        gameID = System.currentTimeMillis();
        dbUtil.createTable(gameID);
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
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                            guardian = false;
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.WHITE));
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.WHITE));
                            gameFinished = true;
                            dbUtil.addMove(gameID, new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE));

                        }
                        else if(move.type() == ClientToServerMessage.Type.PASS) {
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                            guardian = false;
                            passCounter++;
                            if(passCounter >=2) {
                                Player winner = gameEngine.winner();
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, winner));
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, winner));
                                gameFinished = true;
                                dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE));

                            }
                            else{
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.OTHER_PLAYER_PASSED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.BLACK));
                                dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE));
                            }

                        }
                        else {
                            passCounter = 0;
                            if(move.type() == ClientToServerMessage.Type.MAKE_MOVE) {
                                if(gameEngine.isMoveAllowed(move.row(), move.column(), move.player())) {
                                    gameEngine.makeMove(move.row(), move.column(), move.player());
                                    //gameEngine.printBoard();
                                    gameEngine.getBoard().printBoard();
                                    toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                            new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE)
                                            /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                                    toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_MADE,
                                            new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE)
                                            /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.BLACK));
                                    guardian = false;
                                    dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.WHITE));
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
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                            guardian = false;
                            toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.BLACK));
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.BLACK));
                            gameFinished = true;
                            dbUtil.addMove(gameID, new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE));


                        }
                        else if(move.type() == ClientToServerMessage.Type.PASS) {
                            toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                    new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                    /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                            guardian = false;
                            passCounter++;
                            if(passCounter >=2) {
                                Player winner = gameEngine.winner();
                                toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, winner));
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.GAME_FINISHED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, winner));
                                gameFinished = true;
                                dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.NOONE));

                            }
                            else{
                                toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.OTHER_PLAYER_PASSED,
                                        new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                        /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.WHITE));
                                dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK));
                            }

                        }
                        else {
                            passCounter = 0;
                            if(move.type() == ClientToServerMessage.Type.MAKE_MOVE) {
                                if(gameEngine.isMoveAllowed(move.row(), move.column(), move.player())) {
                                    gameEngine.makeMove(move.row(), move.column(), move.player());
                                    gameEngine.printBoard();
                                    toPlayer2.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_SUCCESFULL,
                                            new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                            /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, null));
                                    toPlayer1.writeObject(new ServerToClientMessage(ServerToClientMessage.Type.MOVE_MADE,
                                            new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK)
                                            /*gameEngine.getBoard() / new Board(gameEngine.getBoardContents())*/, Player.WHITE));
                                    guardian = false;
                                    dbUtil.addMove(gameID,new GameState(new Board(gameEngine.getBoardContents()), gameEngine.getWhitePoints(), gameEngine.getBlackPoints(), Player.BLACK));
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
            //Asks about replays
            try{
                List<GameState> gameReplay = dbUtil.getGame(gameID);
                String replay1 = (String) fromPlayer1.readObject();
                if(replay1.equals("YES")){
                    for(GameState g : gameReplay){
                        toPlayer1.writeObject(g);
                    }
                    toPlayer1.writeObject(null);

                }
                String replay2 = (String) fromPlayer2.readObject();
                if(replay2.equals("YES")){
                    for(GameState g : gameReplay){
                        toPlayer2.writeObject(g);
                    }
                    toPlayer2.writeObject(null);
                }
            }
            catch(ClassNotFoundException ex){

                System.err.println(ex);
            }


        } catch (IOException ex) {
            System.err.println(ex);
        }finally {
            HibernateUtil.shutdown();
        }
    }

}

