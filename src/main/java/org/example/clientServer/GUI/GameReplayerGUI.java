package org.example.clientServer.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.example.bot.ClientBot;
import org.example.gameEngine.Board;
import org.example.gameEngine.BoardGUI;
import org.example.models.GameState;
import org.example.sampleDatabase.DatabaseUtils;

import java.util.List;

public class GameReplayerGUI extends Application {

    private Stage primaryStage;
    private BorderPane rootPane;
    private Pane boardPane;
    private List<GameState> game;
    private int index = 0;

    public GameReplayerGUI(List<GameState> game){
        this.game = game;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Replay of game");
        this.primaryStage.setScene(getReplayingScene(this.primaryStage));
        this.primaryStage.show();
    }

    private Scene getReplayingScene(Stage primaryStage){
        System.out.println("Jestem w replayowaniu");

        this.rootPane = new BorderPane();
        this.boardPane = new Pane();
        this.boardPane.setBackground(new Background(new BackgroundFill(Color.SADDLEBROWN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.rootPane.setCenter(boardPane);
/*
        HBox bottom = new HBox(20);
        bottom.setAlignment(Pos.CENTER);
        bottom.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        Button previous = new Button("Poddaj sie");
        previous.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");
        Button next = new Button("Pas");
        next.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");

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

 */

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER);
        top.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        Label white = new Label("Punkty białych: ");
        Label whitePoints = new Label("0");
        white.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        whitePoints.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        Label introducingNextPlayer = new Label("Następny gracz: ");
        introducingNextPlayer.setAlignment(Pos.CENTER_LEFT);
        introducingNextPlayer.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");
        Label nextPlayer = new Label("");
        nextPlayer.setAlignment(Pos.CENTER_RIGHT);
        nextPlayer.setStyle("-fx-background-color: #f4a460; -fx-text-fill: black;");
        //setBackground(new Background(new BackgroundFill(Color.BROWN,null,null)));

        Label black = new Label("Punkty czarnych: ");
        Label blackPoints = new Label("0");
        black.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));
        blackPoints.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN,null,null)));

        white.setFont(new Font(15));
        whitePoints.setFont(new Font(15));
        introducingNextPlayer.setFont(new Font(15));
        nextPlayer.setFont(new Font(15));
        black.setFont(new Font(15));
        blackPoints.setFont(new Font(15));
        top.getChildren().addAll(white, whitePoints, introducingNextPlayer, nextPlayer, black, blackPoints);

        rootPane.setTop(top);

        index = 0;
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(3),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                if(index >=0 && index < game.size()){
                                    Board board = game.get(index).board();
                                    BoardGUI boardGUI = new BoardGUI(board);
                                    boardPane = boardGUI.showUselessBoard(primaryStage.getWidth(), primaryStage.getHeight()-80.0);
                                    rootPane.setCenter(boardPane);
                                    whitePoints.setText(String.valueOf(game.get(index).whitesCaptured()));
                                    blackPoints.setText(String.valueOf(game.get(index).blacksCaptured()));
                                    nextPlayer.setText(game.get(index).nextPlayer().value());
                                }
                                index++;
                            }
                        }
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE); // Ustawienie, aby cyklicznie powtarzał
        timeline.play();



        return new Scene(rootPane,800,800);

    }


}
