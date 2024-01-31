package org.example.clientServer.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.example.bot.Bot;
import org.example.bot.ClientBot;
import org.example.clientServer.ClientInterface;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.gameEngine.Board;
import org.example.gameEngine.BoardGUI;
import org.example.gameEngine.GameEngine;
import org.example.models.Constants;
import org.example.models.GameState;
import org.example.models.Player;
import org.example.sampleDatabase.DatabaseUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientGUI extends Application implements ClientInterface  {
    Socket socket;
    //private boolean myTurn = false;
    private String myToken = " ", otherToken = " ";
    private Player me;
    private BoardGUI board;
    private Label topLabel, white, whitePoints, black, blackPoints;
    private BorderPane rootPane;
    private Pane boardPane;
    private Stage primaryStage;

    private int rowSelected;
    private int columnSelected;

    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private Thread thread;
    private boolean botGameFlag=false;

    private boolean continueToPlay = true;
    private final Object lock = new Object();


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
    public void stop() throws Exception {
        super.stop();
            this.thread.interrupt();


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
                primaryStage.setTitle("GO - gracz czarny");
                primaryStage.setScene(makeChoosingSizeScene(primaryStage));
            }
            else if( me == Player.WHITE) {
                //jezeli bialy, to dowiedz sie na jakiej tablicy grasz
                try{
                    Integer boardSize = (Integer) fromServer.readObject();
                    board = new BoardGUI(boardSize, lock);
                    primaryStage.setTitle("GO - gracz bialy");
                    primaryStage.setScene(makePlayingScene(primaryStage));
                }
                catch (IOException | ClassNotFoundException ex){
                    System.err.println(ex);
                }

            }


        });
        zBotem.setOnAction(event ->{
            connectToServer();
            botGameFlag=true;
            primaryStage.setScene(makeChoosingSizeScene(primaryStage));

        });

        center.getChildren().addAll(zPrzeciwnikiem, zBotem);
        rootPane.setCenter(center);
        return new Scene(rootPane, 800, 800);
    }

    private Scene makeChoosingSizeScene(Stage primaryStage){
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
            board = new BoardGUI(wybranyRozmiar, lock);
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
        this.rootPane = new BorderPane();
        boardPane = board.showBoard(primaryStage.getWidth(), primaryStage.getHeight() - 70.0);
        this.rootPane.setCenter(boardPane);

        HBox bottom = new HBox(15);
        bottom.setAlignment(Pos.CENTER);
        bottom.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        Button surrender = new Button("Poddaj sie");
        surrender.setFont(new Font(15));
        surrender.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");
        Button pass = new Button("Pas");
        pass.setFont(new Font(15));
        pass.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");

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


        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER);
        top.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        white = new Label("Punkty białych: ");
        whitePoints = new Label("0");
        white.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        whitePoints.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        topLabel = new Label("T");
        topLabel.setAlignment(Pos.CENTER);
        topLabel.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");
                //setBackground(new Background(new BackgroundFill(Color.BROWN,null,null)));

        black = new Label("Punkty czarnych: ");
        blackPoints = new Label("0");
        black.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        blackPoints.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        white.setFont(new Font(15));
        whitePoints.setFont(new Font(15));
        topLabel.setFont(new Font(15));
        black.setFont(new Font(15));
        blackPoints.setFont(new Font(15));


        top.getChildren().addAll(white, whitePoints, topLabel, black, blackPoints);


        rootPane.setTop(top);


        this.thread = new Thread(this);
        thread.start();
        if(botGameFlag){
            ClientBot botOpponent = new ClientBot();
        }

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



    @Override
    public void run() {
        try {
            /*
            Platform.runLater(() ->{
                VBox box = new VBox(20);
                this.rootPane.setCenter(box);
                box.getChildren().add(new Button("alfa"));
                box = new VBox(30);
                this.rootPane.setCenter(box);
                box.getChildren().add(new Button("cds"));

            });
             */
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
                Platform.runLater(() -> {
                    topLabel.setText("Oczekiwanie na dolaczenie przeciwnika");
                });


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
                    while(continueToPlay){
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
                    while(continueToPlay) {
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


    public void waitForConditionChange() throws InterruptedException {
        synchronized (lock) {
            while (board.checkMoveEnable()) {
                lock.wait();  // Wątek zostaje zawieszony i czeka na notify()
            }
        }
    }
    private void waitForPlayerAction() throws InterruptedException {
        /*while (board.checkMoveEnable()) {

            //System.out.println(board.checkMoveEnable());
            //boolean i = board.checkMoveEnable();
        }*/
        try{
            waitForConditionChange();
        }
        catch(InterruptedException ex){
            System.err.println(ex);
        }
        System.out.println("DOCZEKAŁEM SIE");
    }





    private boolean sendMove() throws IOException, ClassNotFoundException {
        System.out.println("Jestem w sendMove");

        if (board.getRowSelected() == -3 && board.getColumnSelected() == -3) {
            System.err.println("Zły move");
            return false;
        }
        else if (board.getRowSelected() == -2 && board.getColumnSelected() == -2) {
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
            board.stopMoving();
            Board receivedBoard = feedback.board();
            board.modifyBoard(receivedBoard);
            System.out.println("Wysłany ruch i otrzymany board");
            board.printBoard();
            Platform.runLater(() -> {
                boardPane = board.showBoard(this.primaryStage.getWidth(), this.primaryStage.getHeight()-70.0);
                this.rootPane.setCenter(boardPane);
                topLabel.setText("Oczekiwanie na ruch przeciwnika");
                whitePoints.setText(String.valueOf(feedback.gameState().whitesCaptured()));
                blackPoints.setText(String.valueOf(feedback.gameState().blacksCaptured()));
            });


            System.out.println("Waiting for opponent's move");
            return true;
        }
        else if(feedback.type() == ServerToClientMessage.Type.MOVE_FAILURE) {
            board.okToMove();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(this.primaryStage);
                alert.setTitle("Nieprawidłowy ruch");
                alert.setHeaderText(null);
                alert.setContentText("Wykonaj inny ruch");

                alert.showAndWait();

            });

            //System.out.println("nieudany ruch");
            return false;
        }
        return false;
    }

    private void recieveInfoFromServer() throws IOException {
        try {
            ServerToClientMessage message = (ServerToClientMessage) fromServer.readObject();


            if (message.type() == ServerToClientMessage.Type.GAME_FINISHED) {
                System.out.println("Zakonczenie gry");
                continueToPlay = false;
                if(message.player() == me) {
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! WYGRANA!");
                        whitePoints.setText(String.valueOf(message.gameState().whitesCaptured()));
                        blackPoints.setText(String.valueOf(message.gameState().blacksCaptured()));
                    });
                }
                else if(message.player() == null){
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! REMIS!");
                        whitePoints.setText(String.valueOf(message.gameState().whitesCaptured()));
                        blackPoints.setText(String.valueOf(message.gameState().blacksCaptured()));
                    });
                }
                else {
                    Platform.runLater(() -> {
                        topLabel.setText("KONIEC GRY! PRZEGRANA!");
                        whitePoints.setText(String.valueOf(message.gameState().whitesCaptured()));
                        blackPoints.setText(String.valueOf(message.gameState().blacksCaptured()));
                    });
                }
                Platform.runLater(() -> {
                    showReplayWindow(primaryStage);
                });
            }
            else if(message.type() == ServerToClientMessage.Type.MOVE_MADE) {
                Board receivedBoard = message.board();
                board.modifyBoard(receivedBoard);
                System.out.println("Otrzymany board:");
                board.printBoard();
                message.gameState().printCurrentState();
                System.out.println("my white captured: "+ message.gameState().whitesCaptured());
                System.out.println("my black captured: "+ message.gameState().blacksCaptured());
                board.okToMove();
                Platform.runLater(() -> {
                    boardPane = board.showBoard(this.primaryStage.getWidth(), this.primaryStage.getHeight()-70.0);
                    this.rootPane.setCenter(boardPane);
                    topLabel.setText("TWOJA KOLEJ");
                    whitePoints.setText(String.valueOf(message.gameState().whitesCaptured()));
                    blackPoints.setText(String.valueOf(message.gameState().blacksCaptured()));
                });


                System.out.println("My turn (" + myToken + ")");
            }
            else if(message.type() == ServerToClientMessage.Type.OTHER_PLAYER_PASSED) {
                board.okToMove();
                Platform.runLater(() -> {
                    topLabel.setText("OPONENT PASOWAŁ! TWOJA KOLEJ");
                });
                System.out.println("The opponent passed");
                System.out.println("My turn (" + myToken + ")");
            }
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    }

    private void showReplayWindow(Stage primaryStage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(primaryStage);
        alert.setTitle("Replay?");
        alert.setHeaderText("Czy chcesz zobaczyc replay gry?");
        alert.setContentText("Wybierz jedną z opcji:");

        ButtonType buttonTypeYes = new ButtonType("Tak");
        ButtonType buttonTypeNo = new ButtonType("Nie");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            // Reakcja na odpowiedź "Tak"
            try {
                System.out.println("Chce przebieg gry");
                toServer.writeObject("YES");
                //System.out.println("Chce przebieg gry2");
                List<GameState> game = new ArrayList<>();
                //System.out.println("Chce przebieg gry3");
                GameState state = (GameState) fromServer.readObject();
                //System.out.println("Chce przebieg gry3");
                while(state != null){
                    game.add(state);
                    state = (GameState) fromServer.readObject();
                }
                //System.out.println("Otrzymano przebieg gry");
                for(GameState g : game){
                    g.printCurrentState();
                }
                GameReplayerGUI replayer = new GameReplayerGUI(game);
                replayer.start(this.primaryStage);


            }
            catch(IOException | ClassNotFoundException ex){
                System.err.println(ex);
            }
            catch(Exception e){
                System.err.println(e);
            }

        }
        else {
            // Reakcja na odpowiedź "Nie" lub zamknięcie okna
            try{
                toServer.writeObject("NO");
            }
            catch(IOException ex){
                System.err.println(ex);
            }

        }
    }


}