package org.example;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 *
 * @author aid
 */
public class Server {

    public static final int PLAYER1_BLACK = 1;
    public static final int PLAYER2_WHITE = 2;

    public static void main(String[] args) {
        Server display = new Server();
    }

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
                new DataOutputStream(firstPlayer.getOutputStream()).writeInt(PLAYER1_BLACK);

                //connection to player2
                Socket secondPlayer = serverSocket.accept();
                System.out.println("Player 2 joined session " + sessionNum + ". Player 2's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");
                //notify second player that he is second player
                new DataOutputStream(secondPlayer.getOutputStream()).writeInt(PLAYER2_WHITE);

                //starting the thread for two players
                System.out.println("Starting a thread for session " + sessionNum + "...\n");
                Session game = new Session(firstPlayer, secondPlayer);
                Thread t1 = new Thread(game);
                t1.start();
                sessionNum++;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

}
