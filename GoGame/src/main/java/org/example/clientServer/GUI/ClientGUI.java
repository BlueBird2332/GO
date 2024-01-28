package org.example.clientServer.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.example.clientServer.ClientInterface;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.game.Board;
import org.example.game.BoardGUI;
import org.example.game.Constants;
import org.example.game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientGUI extends Application implements ClientInterface  {
    Socket socket;
    //private boolean myTurn = false;
    private String myToken = " ", otherToken = " ";
    private Player me;
    private BoardGUI board;
    private Label topLabel;
    private Pane boardPane;
    private Stage primaryStage;

    private int rowSelected;
    private int columnSelected;

    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private Thread thread;

    private boolean continueToPlay = true;
    private boolean myTurn = false;


    //TODO:
    //  -może klasy eksperckie do wybieranie, ruszania, odbierania
    //  - \> łatwiejsze zmiany, na wspólnym interejsie
    //  - jakiś builder dla gui?

    public static void main(String[] args) {
        launch();
    }


    public ClientGUI() {
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("GO - player client");
        this.primaryStage.setScene(makeInitScene(this.primaryStage));
        this.primaryStage.show();
    }
    private Scene makeInitScene(Stage primaryStage) {
        /*TODO:
            - o ile pierwszy lub bot - wybór rozmiaru tablicy
         */
        BorderPane rootPane = new BorderPane();
        VBox center = new VBox(40.0);
        center.setAlignment(Pos.CENTER);
        Button zPrzeciwnikiem = new Button("Graj przeciw człowiekowi");
        Button zBotem = new Button("Graj przeciw komputerowi");

        zPrzeciwnikiem.setOnAction(event ->{
            connectToServer();
            if(me == Player.BLACK) {
                //jezeli czarny, to wybierz rozmiar tablicy do gry
                primaryStage.setScene(makeChoosingSizeScene(primaryStage));
            }
            else if( me == Player.WHITE) {
                //jezeli bialy, to dowiedz sie na jakiej tablicy grasz
                try{
                    Integer boardSize = (Integer) fromServer.readObject();
                    board = new BoardGUI(boardSize);
                    primaryStage.setScene(makePlayingScene(primaryStage));
                }
                catch (IOException | ClassNotFoundException ex){
                    System.err.println(ex);
                }

            }


        });
        zBotem.setOnAction(event ->{
            connectToServer();
            primaryStage.setScene(makeChoosingSizeScene(primaryStage));
            //TODO:
            //  - trzeba jakos podłączyć bota
        });

        center.getChildren().addAll(zPrzeciwnikiem, zBotem);
        rootPane.setCenter(center);
        return new Scene(rootPane, 800, 800);
    }

    private Scene makeChoosingSizeScene(Stage primaryStage){
        //TODO:
        //  - pokaż tablicę więc
        BorderPane rootPane = new BorderPane();
        VBox center = new VBox(40.0);
        center.setAlignment(Pos.CENTER);

        Label wybierzRozmiarLabel = new Label("Wybierz rozmiar tablicy do gry");
        ComboBox<Integer> wybierzRozmiarComboBox = new ComboBox<>();
        wybierzRozmiarComboBox.getItems().addAll(19, 13, 9);
        wybierzRozmiarComboBox.setValue(19);
        Button confirmButton = new Button("Potwierdz wybor");
        confirmButton.setOnAction(ev -> {
            Integer wybranyRozmiar = wybierzRozmiarComboBox.getValue();
            board = new BoardGUI(wybranyRozmiar);
            try {
                toServer.writeObject(wybranyRozmiar);
            }
            catch(IOException ex){
                System.err.println(ex);
            }
            primaryStage.setScene(makePlayingScene(primaryStage));
        });

        center.getChildren().addAll(wybierzRozmiarLabel, wybierzRozmiarComboBox, confirmButton);

        rootPane.setCenter(center);
        return new Scene(rootPane, 800, 800);
    }

    private Scene makePlayingScene(Stage primaryStage){
        //TODO:
        // - ogarnąć jak się gra xDDD
        //  - opakuj Board GuiBordem
        //  - za każdym razem rysuj nowy? Tak chyba wygodniej. Albo po prostu przejdź po kamieniach i je zaktualizuj
        //  - W GUIBoardzie trzymaj kamienie, z setOnAction ztobionym - że się Row i Column wybiera, jeżeli my turn? i wysyła i supeancko
        //  - a jak odbierasz boarda, to ogarniasz z niego jak pokazać chyba
        BorderPane rootPane = new BorderPane();
        boardPane = board.showBoard(primaryStage.getWidth(), primaryStage.getHeight() - 60.0);
        rootPane.setCenter(boardPane);

        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        Button surrender = new Button("Poddaj sie");
        Button pass = new Button("Pas");

        surrender.setOnAction(event -> {
            if(board.checkMoveEnable()){
                board.surrender();
            }
        });
        pass.setOnAction(event -> {
            if(board.checkMoveEnable()){
                board.pass();
            }
        });
        bottom.getChildren().addAll(pass, surrender);
        rootPane.setBottom(bottom);

        topLabel = new Label("JAKIS TEKST");

        rootPane.setTop(topLabel);

        //TODO: CZY TEN THREAD TUTAJ TO W OGÓLE DOBRY POMYSŁ???
        this.thread = new Thread(this);
        thread.start();
        //Platform.runLater - runningGame

        return new Scene(rootPane,800,800);
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

            //read which player
            //System.out.println("Proba startu");
            me = (Player) fromServer.readObject();

        }
        catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }

    }

    private void runningGame(Stage primaryStage){

    }


    @Override
    public void run() {
        try {
            //TODO: kiedy było czeknie na wybór planszy i bota?
            //TODO: GUI
            //  - zmień scenerię na planszę

            //System.out.println(me);


            //if first player set the token to X and wait for second player to join
            if(me == Player.BLACK) {

                myToken = Constants.BLACK.value();
                otherToken = Constants.WHITE.value();

                System.out.println("Player 1 with token 'B'");
                System.out.println("Waiting for player 2 to join");
                topLabel.setText("Oczekiwanie na dolaczenie przeciwnika");

                //notification that player 2 joined
                Player otherPlayer = (Player) fromServer.readObject();
                Platform.runLater(() -> {
                    topLabel.setText("Przeciwnik dolaczyl, twoj ruch");
                    board.okToMove();
                });

                System.out.println("Player 2 has joined. I start first");



//IDEA: jezeli ja zaczynam, to ustawiam enable to move na ok, myTurn na true
                //myTurn = true;
            }
            //if second player then game can start
            else if (me == Player.WHITE) {
                myToken = Constants.WHITE.value();
                otherToken = Constants.BLACK.value();

                System.out.println("Player 2 with token 'W'");
                //System.out.println("T\nype two numbers to place there your stone");
                //System.out.println("Type -1 -1 to pass");
                //System.out.println("PType -2 -2 to surrender");
                System.out.println("\nWaiting for player 1 to move");
                Platform.runLater(() -> {
                    topLabel.setText("Oczekiwanie na ruch przeciwnika");
                    board.stopMoving();
                });

  //IDEA: jezeli nie ja zaczynam, to ustawiam enable to move na NIEok, myTurn na false
            }

            while (continueToPlay) {
                if (me == Player.BLACK) {
                    while(true){
                        waitForPlayerAction(); //dopóki jest enable to move to czekaj
                        if(sendMove() == false) { //jeżeli był zły move
                            System.out.println("Illegal move, try again."); //to zrób enable znowu i super
                            continue;
                        }//jeżeli się udało ruszyć, to enable jest false i czekamy na ruch oponenta
                        //TRZEBA JAKOS ZAKTUALIOWAC WYSWIETLANIE
                        recieveInfoFromServer();    //otrzymujemy ruch oponenta, AJKOS WYSWIETL, zrob enable to move i czekaj
                        break;
                    }

                }
                else if (me == Player.WHITE) {
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
            while (board.checkMoveEnable()) {
                System.out.println(board.checkMoveEnable());
            }
            System.out.println("DOCZEKAŁEM SIE");
    }

    private boolean sendMove() throws IOException, ClassNotFoundException {
        System.out.println("Jestem w sendMove");

        if (board.getRowSelected() == -2 && board.getColumnSelected() == -2) {
            toServer.writeObject (new ClientToServerMessage(ClientToServerMessage.Type.SURRENDER, rowSelected,columnSelected, me));
        }
        else if (board.getRowSelected() == -1 && board.getColumnSelected() == -1) {
            toServer.writeObject (new ClientToServerMessage(ClientToServerMessage.Type.PASS, rowSelected,columnSelected, me));
        }
        else {
            //System.out.println("wysyłam ruch");
            toServer.writeObject(new ClientToServerMessage(ClientToServerMessage.Type.MAKE_MOVE, board.getRowSelected(), board.getColumnSelected(), me));
            //System.out.println("wysłałem ruch");
        }

        ServerToClientMessage feedback = (ServerToClientMessage) fromServer.readObject();
        if(feedback.type() == ServerToClientMessage.Type.MOVE_SUCCESFULL) {
            Board returnBoard = feedback.board();
            board = new BoardGUI(returnBoard);
            board.getBoardBoard().printBoard();
            Platform.runLater(() -> {
                boardPane = board.showBoard(this.primaryStage.getWidth(), this.primaryStage.getHeight()-60.0);
                topLabel.setText("Oczekiwanie na ruch przeciwnika");
            });


            System.out.println("Waiting for opponent's move");
            return true;
        }
        if(feedback.type() == ServerToClientMessage.Type.MOVE_FAILURE) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Nieprawidłowy ruch");
                alert.setHeaderText(null);
                alert.setContentText("Wykonaj inny ruch");

                alert.showAndWait();
                board.okToMove();
            });

            //System.out.println("nieudany ruch");
            return false;
        }
        return false;
    }

    private void recieveInfoFromServer() throws IOException {
        try {
            ServerToClientMessage message = (ServerToClientMessage) fromServer.readObject();
            //TODO: GUI
            //  - oganąć to wszystko
            if (message.type() == ServerToClientMessage.Type.GAME_FINISHED) {

                continueToPlay = false;
                if(message.player() == me) {
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! WYGRANA!");
                    });
                }
                else if(message.player() == null){
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! REMIS!");
                    });
                }
                else {
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! PRZEGRANA!");
                    });
                }
            }
            else if(message.type() == ServerToClientMessage.Type.MOVE_MADE) {
                Board receivedBoard = message.board();
                board = new BoardGUI(receivedBoard);
                Platform.runLater(() -> {
                    boardPane = board.showBoard(this.primaryStage.getWidth(), this.primaryStage.getHeight()-60.0);
                    board.okToMove();
                    topLabel.setText("TWOJA KOLEJ");
                });


                System.out.println("My turn (" + myToken + ")");
            }
            else if(message.type() == ServerToClientMessage.Type.OTHER_PLAYER_PASSED) {
                Platform.runLater(() -> {
                    topLabel.setText("OPONENT PASOWAŁ! TWOJA KOLEJ");
                    board.okToMove();
                });
                System.out.println("The opponent passed");
                System.out.println("My turn (" + myToken + ")");
            }
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    }


}