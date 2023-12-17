package org.example;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author aid
 */
public class Client implements Runnable {

    //declaring constants
    public static final int PLAYER1_BLACK = 1;
    public static final int PLAYER2_WHITE = 2;

    //public static final int PLAYER1_WON = 1;
    //public static final int PLAYER2_WON = 2;
    public static final int DRAW = 3;


    Socket socket;
    private boolean myTurn = false;
    private char myToken = ' ', otherToken = ' ';
    //TODO: make sure we have the board class
    private Board board;

    private int rowSelected;
    private int columnSelected;

    private DataInputStream fromServer;
    private DataOutputStream toServer;

    private boolean continueToPlay = true;
    //private boolean waiting = true;
    private Scanner scanner;


    public static void main(String[] args) {
        Client display = new Client();
        display.init();
    }


    public void init() {

        //TODO: rozne wielkosci tablicy, to juz przy GUI
        board.init(19);
        connectToServer();
        scanner = new Scanner(System.in);
    }

    //method for connecting to server
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
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
            int player = fromServer.readInt();

            //if first player set the token to X and wait for second player to join
            if(player == PLAYER1_BLACK) {
                myToken = 'B';
                otherToken = 'W';
                System.out.println("Player 1 with token 'B'");
                System.out.println("Waiting for player 2 to join");

                //notification that player 2 joined
                fromServer.readInt();
                System.out.println("Player 2 has joined. I start first");

                myTurn = true;
            }
            //if second player then game can start
            else if (player == PLAYER2_WHITE) {
                myToken = 'W';
                otherToken = 'B';
                System.out.println("Player 2 with token '0'");
                System.out.println("Waiting for player 1 to move");
            }

            while (continueToPlay) {
                if (player == PLAYER1_BLACK) {
                    waitForPlayerAction();
                    sendMove();
                    recieveInfoFromServer();
                }
                else if (player == PLAYER2_WHITE) {
                    recieveInfoFromServer();
                    waitForPlayerAction();
                    sendMove();
                }
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        } catch (InterruptedException ex) {}
    }

    private void waitForPlayerAction() throws InterruptedException {

        boolean guard = true;
        while (guard) {
            try {
                rowSelected = scanner.nextInt();
                columnSelected = scanner.nextInt();
                guard = false;
            }
            catch (Exception ex) {
                System.out.println("Nieprawidlowa dana, sprobuj jeszcze raz.");
                String unused = scanner.nextLine();
            }
        }
    }

    private void sendMove() throws IOException {
        toServer.writeInt(rowSelected);
        toServer.writeInt(columnSelected);
    }

    private void recieveInfoFromServer() throws IOException {
        int status = fromServer.readInt();
        if (status == PLAYER1_BLACK) {
            continueToPlay = false;
            if (myToken == 'B') {
                System.out.println("I Won! (BLACK)");
            }
            else if (myToken == 'W') {
                System.out.println("Player 1 (BLACK) has won!");
                recieveMove();
            }
        }
        else if (status == PLAYER2_WHITE) {
            continueToPlay = false;
            if (myToken == 'W') {
                System.out.println("I Won! (WHITE)");
            }
            else if (myToken == 'B') {
                System.out.println("Player 2 (WHITE) has won!");
                recieveMove();
            }
        }
        else if (status == DRAW) {
            continueToPlay = false;
            System.out.println("Game is over, no winner!");

            /* nie przyjmujemy już tego ruch, jezeliwynik to remis, to znaczy, ze tamten spasowal
            if (myToken == '0') {
                recieveMove();
            }*/
        }
        else {
            recieveMove();
            System.out.println("My turn");
            myTurn = true;
        }
    }

    private void recieveMove() throws IOException {
        int row = fromServer.readInt();
        int column = fromServer.readInt();
        //TODO: sprawdź czy to nie pas albo surender
        //TODO: Board musi miec
        // - jakas funkcje updatujaca
        // - jakas metode pokazujaca
        board.updateBoard(row, column, otherToken);
        System.out.println("Otrzymano ruch");
        board.show();
    }

}
