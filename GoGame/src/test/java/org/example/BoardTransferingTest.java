package org.example;

import org.example.game.Board;
import org.example.game.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BoardTransferingTest {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Socket clientOnServer;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toClient;

    @Before
    public void setUp() throws IOException {
        // Inicjalizacja serwera i klienta przed każdym testem
        serverSocket = new ServerSocket(8001);
        clientSocket = new Socket("localhost", 8001);
        clientOnServer = serverSocket.accept();

        toServer = new ObjectOutputStream(clientSocket.getOutputStream());
        toClient = new ObjectOutputStream(clientOnServer.getOutputStream());
        fromServer = new ObjectInputStream(clientSocket.getInputStream());

    }

    @Test
    public void testBoardTransfer() throws IOException, ClassNotFoundException {
        // Test przesyłania obiektu Board od serwera do klienta
        Board expectedBoard = new Board(19);
        expectedBoard.modifyBoard(3,4, Constants.BLACK);
        expectedBoard.modifyBoard(3,6, Constants.WHITE);


        // Wysyłanie obiektu do klienta
        toClient.writeObject(expectedBoard);

        // Odbieranie obiektu od klienta
        Board receivedBoard = (Board) fromServer.readObject();

        // Sprawdzanie, czy otrzymany obiekt jest taki sam, jak oczekiwany
        assertEquals(expectedBoard.getBoard(), receivedBoard.getBoard());
    }

    @After
    public void tearDown() throws IOException {
        // Zamykanie połączeń po każdym teście
        fromServer.close();
        toServer.close();
        clientSocket.close();
        serverSocket.close();
    }

    @Test
    public void testBoardMultipleTransfera() throws IOException, ClassNotFoundException {
        // Test przesyłania obiektu Board od serwera do klienta
        Board expectedBoard = new Board(19);
        expectedBoard.modifyBoard(3,4, Constants.BLACK);
        expectedBoard.modifyBoard(3,6, Constants.WHITE);

        // Wysyłanie obiektu do klienta
        toClient.writeObject(expectedBoard);

        // Odbieranie obiektu od klienta
        Board receivedBoard = (Board) fromServer.readObject();

        // Sprawdzanie, czy otrzymany obiekt jest taki sam, jak oczekiwany
        assertEquals(expectedBoard.getBoard(), receivedBoard.getBoard());


        expectedBoard.modifyBoard(1,1,Constants.BLACK);
        toClient.writeObject(expectedBoard);
        Board receivedBoard2 = (Board) fromServer.readObject();

        // Sprawdzanie, czy otrzymany obiekt jest taki sam, jak oczekiwany
        assertNotEquals(expectedBoard.getBoard(), receivedBoard2.getBoard());

        toClient.reset();
        toClient.writeObject(expectedBoard);
        Board receivedBoard3 = (Board) fromServer.readObject();

        // Sprawdzanie, czy otrzymany obiekt jest taki sam, jak oczekiwany
        assertEquals(expectedBoard.getBoard(), receivedBoard3.getBoard());

    }


}