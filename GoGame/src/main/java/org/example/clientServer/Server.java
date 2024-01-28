package org.example.clientServer;

import org.example.game.Player;

import java.io.*;
import java.net.*;

/**
 *
 * @author aid
 */
public class Server implements ServerInterface{


    /*
    public static void main(String[] args) {

        Server display = new Server();
    }
    */

    public Server() {

        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server started at socket 8000\n");
            int sessionNum = 1;
            while (true) {

                System.out.println("Waiting for players to join session " + sessionNum + "\n");
                //connection to player1
                Socket firstPlayer = serverSocket.accept();
                System.out.println("Player 1 joined session " + sessionNum + ". Player 1's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");
                //notify first player that he is first player
                ObjectOutputStream toPlayer1 = new ObjectOutputStream(firstPlayer.getOutputStream());
                toPlayer1.writeObject(Player.BLACK);
                //TODO:
                //  - oganoąć kiedy i jak podłączać bota - na wysokości clienta? Bot chyba powinien działać na kliencie, być traktowany jak gracz
                //connection to player2
                Socket secondPlayer = serverSocket.accept();
                System.out.println("Player 2 joined session " + sessionNum + ". Player 2's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");
                //notify second player that he is second player
                ObjectOutputStream toPlayer2 = new ObjectOutputStream(secondPlayer.getOutputStream());
                toPlayer2.writeObject(Player.WHITE);

                //starting the thread for two players
                System.out.println("Starting a thread for session " + sessionNum + "...\n");
                Session game = new Session(firstPlayer, toPlayer1, secondPlayer, toPlayer2);
                Thread t1 = new Thread(game);
                t1.start();
                sessionNum++;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}
