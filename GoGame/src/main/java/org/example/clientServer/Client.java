package org.example.clientServer;

import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.game.Board;
import org.example.game.Constants;
import org.example.game.Player;

import java.net.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

/**
 *
 * @author aid
 */
public class Client implements Runnable {

    //declaring constants
    //public static final int PLAYER1_BLACK = 1;
    //public static final int PLAYER2_WHITE = 2;

    //public static final int PLAYER1_WON = 1;
    //public static final int PLAYER2_WON = 2;
    //public static final int DRAW = 3;


    Socket socket;
    //private boolean myTurn = false;
    private String myToken = " ", otherToken = " ";
    private Player me;
    private Board board;

    private int rowSelected;
    private int columnSelected;

    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    private boolean continueToPlay = true;
    //private boolean waiting = true;
    private Scanner scanner;


    public static void main(String[] args) {
        Client client = new Client();
        //display.initialize();
    }

    Client() {
        //TODO: rozne wielkosci tablicy, to juz przy GUI
        board = new Board(11);
        connectToServer();
        scanner = new Scanner(System.in);
    }
    /*public void initialize() {
    }*/

    //method for connecting to server
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 8000);
            //System.out.println("Mamy polączenie");
            toServer = new ObjectOutputStream(socket.getOutputStream());
            //System.out.println("Mamy output");
            fromServer = new ObjectInputStream(socket.getInputStream());
            //System.out.println("Mamy input");

        }
        catch (IOException ex) {
            System.err.println(ex);
        }

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            //read which player
            //System.out.println("Proba startu");
            Player player = (Player) fromServer.readObject();
            me = player;
            //System.out.println(me);

            //if first player set the token to X and wait for second player to join
            if(player == Player.BLACK) {

                myToken = Constants.BLACK.value();
                otherToken = Constants.WHITE.value();

                System.out.println("Player 1 with token 'B'");
                System.out.println("Waiting for player 2 to join");

                //notification that player 2 joined
                Player otherPlayer = (Player) fromServer.readObject();
                System.out.println("Player 2 has joined. I start first");

                //myTurn = true;
            }
            //if second player then game can start
            else if (player == Player.WHITE) {
                myToken = Constants.WHITE.value();
                otherToken = Constants.BLACK.value();

                System.out.println("Player 2 with token 'W'");
                System.out.println("Waiting for player 1 to move");
            }

            while (continueToPlay) {
                if (player == Player.BLACK) {
                    while(true){
                        waitForPlayerAction();
                        if(sendMove() == false) {
                            System.out.println("Illegal move, try again.");
                            continue;
                        }
                        recieveInfoFromServer();
                        break;
                    }

                }
                else if (player == Player.WHITE) {
                    recieveInfoFromServer();
                    while(true) {
                        waitForPlayerAction();
                        if(sendMove() == true) {
                            break;
                        }
                        else {
                            System.out.println("Illegal move, try again.");
                        }
                    }



                }
            }
        }
        catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }catch (InterruptedException ex) {

        }
    }

    private void waitForPlayerAction() throws InterruptedException {

        boolean guard = true;
        while (guard) {
            try {
                rowSelected = scanner.nextInt();
                columnSelected = scanner.nextInt();
                //System.out.println("Odczytałem ruch: " + rowSelected + " " + columnSelected );
                guard = false;
            }
            catch (Exception ex) {
                System.out.println("Wrong data, try again.");
                String unused = scanner.nextLine();
            }
        }
    }

    private boolean sendMove() throws IOException, ClassNotFoundException {

        if (rowSelected == -2 && columnSelected == -2) {
            toServer.writeObject (new ClientToServerMessage(ClientToServerMessage.Type.SURRENDER, rowSelected,columnSelected, me));
        }
        else if (rowSelected == -1 && columnSelected == -1) {
            toServer.writeObject (new ClientToServerMessage(ClientToServerMessage.Type.PASS, rowSelected,columnSelected, me));
        }
        else {
            //System.out.println("wysyłam ruch");
            toServer.writeObject(new ClientToServerMessage(ClientToServerMessage.Type.MAKE_MOVE, rowSelected, columnSelected, me));
            //System.out.println("wysłałem ruch");
        }

        ServerToClientMessage feedback = (ServerToClientMessage) fromServer.readObject();
        if(feedback.type() == ServerToClientMessage.Type.MOVE_SUCCESFULL) {
            //System.out.println("udany ruch");
            return true;
        }
        if(feedback.type() == ServerToClientMessage.Type.MOVE_FAILURE) {
            //System.out.println("nieudany ruch");
            return false;
        }
        return false;
    }

    private void recieveInfoFromServer() throws IOException {
        try {
            ServerToClientMessage message = (ServerToClientMessage) fromServer.readObject();
            if (message.type() == ServerToClientMessage.Type.GAME_FINISHED) {

                continueToPlay = false;
                if(message.player() == me) {
                    System.out.println("I WON!!!");
                }
                else if(message.player() == null){
                    System.out.println("It's a DRAW, noone has won!!!");
                }
                else {
                    System.out.println("I LOST!!!");
                }
            }
            else if(message.type() == ServerToClientMessage.Type.MOVE_MADE) {
                board = null;
                board = message.board();
                board.printBoard();
                System.out.println("My turn");
            }
            else if(message.type() == ServerToClientMessage.Type.OTHER_PLAYER_PASSED) {
                board = null;
                board = message.board();
                board.printBoard();
                System.out.println("The opponent passed");
                System.out.println("My turn");
            }
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    }


}
