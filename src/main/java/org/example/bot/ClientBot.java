package org.example.bot;

import org.example.clientServer.ClientInterface;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.gameEngine.Board;
import org.example.gameEngine.GameEngine;
import org.example.models.Constants;
import org.example.models.Move;
import org.example.models.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientBot implements ClientInterface {

        Socket socket;
        //private boolean myTurn = false;
        private String myToken = " ", otherToken = " ";
        private Player me;
        private Bot bot;
        private Board board;

        private int rowSelected;
        private int columnSelected;

        private ObjectInputStream fromServer;
        private ObjectOutputStream toServer;
        private Thread thread;

        private boolean continueToPlay = true;
        //private boolean waiting = true;


    /*
    public static void main(String[] args) {
        Client client = new Client();
        //display.initialize();
    }
    */

        public ClientBot(int size) {

            board = new Board(size);
            bot = new Bot(new GameEngine(size));
            connectToServer();
        }
        public ClientBot(){
            connectToServer();
        }


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

            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            try {
                //read which player
                //System.out.println("Proba startu");
                Player player = (Player) fromServer.readObject();
                me = player;
                System.out.println("BOT: " + me);

                //if first player set the token to X and wait for second player to join
                if(player == Player.BLACK) {

                    myToken = Constants.BLACK.value();
                    otherToken = Constants.WHITE.value();

                    System.out.println("BOT: Player 1 with token 'B'");
                    System.out.println("BOT: Waiting for player 2 to join");

                    //notification that player 2 joined
                    Player otherPlayer = (Player) fromServer.readObject();
                    System.out.println("BOT: Player 2 has joined. I start first");
                    //System.out.println("\nType two numbers to place there your stone");
                    //System.out.println("Type -1 -1 to pass");
                    //System.out.println("PType -2 -2 to surrender");


                    //myTurn = true;
                }
                //if second player then game can start
                else if (player == Player.WHITE) {
                    Integer boardSize = (Integer) fromServer.readObject();
                    board = new Board(boardSize);
                    bot = new Bot(new GameEngine(boardSize));

                    myToken = Constants.WHITE.value();
                    otherToken = Constants.BLACK.value();

                    System.out.println("BOT: Player 2 with token 'W'");
                    //System.out.println("T\nype two numbers to place there your stone");
                    //System.out.println("Type -1 -1 to pass");
                    //System.out.println("PType -2 -2 to surrender");
                    System.out.println("\nBOT: Waiting for player 1 to move");
                }

                while (continueToPlay) {
                    if (player == Player.BLACK) {
                        while(true){
                            waitForPlayerAction();
                            if(sendMove() == false) {
                                System.out.println("BOT: Illegal move, try again.");
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
                                System.out.println("BOT: Illegal move, try again.");
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
            //TODO: no ogólnie to nie zawsze tam mi ruchy zwraca, więc niefajnie, do porawy
            Move sugestedMove = bot.findBestMove(2,me);
            if (sugestedMove == null){
                sugestedMove = new Move(-1,-1,me);
            }
            rowSelected = sugestedMove.row();
            columnSelected = sugestedMove.column();

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
                bot.receiveMove(feedback.board(), me);
                System.out.println("BOT: Waiting for opponent's move");
                return true;
            }
            if(feedback.type() == ServerToClientMessage.Type.MOVE_FAILURE) {

                rowSelected = -2;
                columnSelected = -2;
                return sendMove();
            }
            return false;
        }

        private void recieveInfoFromServer() throws IOException {
            try {
                ServerToClientMessage message = (ServerToClientMessage) fromServer.readObject();

                if (message.type() == ServerToClientMessage.Type.GAME_FINISHED) {

                    continueToPlay = false;
                    if(message.player() == me) {
                        System.out.println("BOT: I WON!!!");
                    }
                    else if(message.player() == null){
                        System.out.println("BOT: It's a DRAW, noone has won!!!");
                    }
                    else {
                        System.out.println("BOT: I LOST!!!");
                    }
                    showReplayWindow();
                    this.thread.interrupt();
                }
                else if(message.type() == ServerToClientMessage.Type.MOVE_MADE) {
                    board = message.board();
                    board.printBoard();
                    bot.receiveMove(message.board(), Player.getOpponent(me));
                    System.out.println("BOT: My turn (" + myToken + ")");
                }
                else if(message.type() == ServerToClientMessage.Type.OTHER_PLAYER_PASSED) {
                    board = message.board();
                    board.printBoard();
                    bot.finish();
                    System.out.println("BOT: The opponent passed");
                    System.out.println("BOT: My turn (" + myToken + ")");
                }
            }
            catch (Exception ex) {
                System.err.println(ex);
            }
        }

        private void showReplayWindow(){
            //would you like to watch replay
            try{
                toServer.writeObject("NO");
            }
            catch(IOException ex){
                System.err.println(ex);
            }
        }


}


