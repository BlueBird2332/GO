package org.sampleDatabase;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.models.GameState;
import org.models.GameStatus;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils implements IDataBaseUtil {

    @Override
    public List<GameState> getGame(int gameId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<GameState> allMoves = new ArrayList<>();
        try {
            String tableName = "gamestatus" + gameId;
            GameStatus gs = new GameStatus();

            // Retrieve moves from the database
            @SuppressWarnings("unchecked")
            List<GameStatus> gameStatusList = session.createNativeQuery("SELECT * FROM " + tableName, GameStatus.class)
                    .getResultList();
            //System.out.println(gameStatusList.get(0).getBoard());
            for (GameStatus gameStatus : gameStatusList) {
                allMoves.add(StatusStateConverter.getGameStateFromStatus(gameStatus));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return allMoves;
    }

    @Override
    public GameState getSingleMove(int gameId, int moveId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        GameState move = null;
        try {
            String tableName = "gamestatus" + gameId;

            // Retrieve moves from the database
            @SuppressWarnings("unchecked")
            GameStatus gameStatus = session.createNativeQuery("SELECT * FROM " + tableName + " WHERE id = " + moveId, GameStatus.class).getSingleResult();

            move = StatusStateConverter.getGameStateFromStatus(gameStatus);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return move;
    }

    @Override
    public void addMove(int gameId, GameState gameState) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Construct the dynamic table name
            String tableName = "GameStatus" + gameId;

            // Create a new move object
            GameStatus gameStatus = StatusStateConverter.getGameStatusFromState(gameState);

            // Save the move to the database using native SQL
            session.createNativeQuery("INSERT INTO " + tableName + " (board, nextPlayer, blacksCaptured, whitesCaptured) VALUES (?, ?, ?, ?)")
                    .setParameter(1, gameStatus.getBoard())
                    .setParameter(2, gameStatus.getNextPlayer())
                    .setParameter(3, gameStatus.getBlacksCaptured())
                    .setParameter(4, gameStatus.getWhitesCaptured())
                    .executeUpdate();

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void createTable(int gameId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Transaction transaction = session.beginTransaction();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS GameStatus" + gameId + " ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "board VARCHAR(512) NOT NULL,"
                    + "nextPlayer VARCHAR(512) NOT NULL,"
                    + "blacksCaptured INT,"
                    + "whitesCaptured INT"
                    + ")";

            // Execute the SQL statement to create the table
            session.createNativeQuery(createTableSQL).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
