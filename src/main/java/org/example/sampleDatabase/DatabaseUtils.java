package org.example.sampleDatabase;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.example.models.GameState;
import org.example.models.GameStatus;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils implements IDataBaseUtil {

    @Override
    public List<GameState> getGame(long gameId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<GameState> allMoves = new ArrayList<>();
        try {
            String tableName = "gamestatus" + gameId;
            GameStatus gs = new GameStatus();

            // Retrieve moves from the database
            @SuppressWarnings("unchecked")
            List<GameStatus> gameStatusList = session.createNativeQuery("SELECT * FROM " + tableName, GameStatus.class)
                    .getResultList();

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
    public GameState getSingleMove(long gameId, int moveId) {
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
    public void addMove(long gameId, GameState gameState) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Construct the dynamic table name
            String tableName = "gamestatus" + gameId;

            GameStatus gameStatus = StatusStateConverter.getGameStatusFromState(gameState);

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

    public void createTable(long gameId) {
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

            session.createNativeQuery(createTableSQL).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
