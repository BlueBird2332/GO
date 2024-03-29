package org.sampleDatabase;

import org.gameEngine.GameEngine;
import org.models.GameState;
import org.models.GameStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.models.Move;
import org.models.Player;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialize Hibernate
            HibernateUtil.getSessionFactory();
            DatabaseUtils dbUtils = new DatabaseUtils();
            int gameId = 4;
            // Add a move to the database
            dbUtils.createTable(gameId);

            GameEngine ge = new GameEngine(19);
            ge.makeMove(new Move(1,0, Player.BLACK));
            var state = ge.getCurrentState();
            dbUtils.addMove(gameId, state);
            ge.makeMove(new Move(2,0, Player.WHITE));
            state = ge.getCurrentState();
            dbUtils.addMove(gameId, state);

            var game = dbUtils.getGame(4);
            for(GameState g : game){
                g.printCurrentState();
            }

            var move = dbUtils.getSingleMove(4, 1);
            move.printCurrentState();
        } finally {
            // Shutdown Hibernate
            HibernateUtil.shutdown();
        }
    }
}
