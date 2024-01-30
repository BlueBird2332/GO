package org.clientServer.GUI;

import org.clientServer.ServerInterface;
import org.clientServer.SessionInterface;
import org.models.Player;

import java.io.*;
import java.net.*;

/**
 *
 * @author aid
 */
public class ServerForGUI implements ServerInterface {


    /*
    public static void main(String[] args) {

        Server display = new Server();
    }
    */

    public ServerForGUI() {

        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server started at socket 8000\n");
            int sessionNum = 1;
            //TODO: nie róbmy serwera ciągłego
            while (true) {

                System.out.println("Waiting for players to join session " + sessionNum + "\n");
                //connection to player1
                Socket firstPlayer = serverSocket.accept();
                System.out.println("Player 1 joined session " + sessionNum + ". Player 1's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");
                //notify first player that he is first player
                ObjectOutputStream toPlayer1 = new ObjectOutputStream(firstPlayer.getOutputStream());
                ObjectInputStream fromPlayer1 = new ObjectInputStream(firstPlayer.getInputStream());

                toPlayer1.writeObject(Player.BLACK);
                Integer boardSize = (Integer) fromPlayer1.readObject();
                //TODO:
                //  - oganoąć kiedy i jak podłączać bota - na wysokości clienta? Bot chyba powinien działać na kliencie, być traktowany jak gracz
                //connection to player2
                Socket secondPlayer = serverSocket.accept();
                System.out.println("Player 2 joined session " + sessionNum + ". Player 2's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");
                //notify second player that he is second player
                ObjectOutputStream toPlayer2 = new ObjectOutputStream(secondPlayer.getOutputStream());
                toPlayer2.writeObject(Player.WHITE);
                toPlayer2.writeObject(boardSize);

                //starting the thread for two players
                System.out.println("Starting a thread for session " + sessionNum + "...\n");
                SessionInterface game = new SessionForGUI(firstPlayer, toPlayer1, fromPlayer1, secondPlayer, toPlayer2, boardSize);
                Thread t1 = new Thread(game);
                t1.start();
                sessionNum++;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }

    }

}
