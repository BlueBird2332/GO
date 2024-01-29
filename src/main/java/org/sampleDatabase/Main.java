package org.sampleDatabase;

import org.models.GameStatus;
import org.models.Move;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialize Hibernate
            HibernateUtil.getSessionFactory();

            // Add a move to the database
            addMove("Initial Board State", "Player X", 2, 3);

            // Retrieve and print moves from the database
            retrieveAndPrintMoves();
        } finally {
            // Shutdown Hibernate
            HibernateUtil.shutdown();
        }
    }

    private static void addMove(String board, String nextPlayer, int blacksCaptured, int whitesCaptured) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Create a new Move object
            GameStatus gameStatus= new GameStatus(board, nextPlayer, blacksCaptured, whitesCaptured);

            // Save the move to the database
            session.persist(gameStatus);

            // Commit the transaction
            transaction.commit();
            System.out.println("Move added successfully.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private static void retrieveAndPrintMoves() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // Retrieve moves from the database
            @SuppressWarnings("unchecked")
            List<GameStatus> gameStatusList = session.createQuery("FROM GameStaus").list();

            // Print retrieved moves
            System.out.println("Retrieved Moves:");
            for (GameStatus gameStatus : gameStatusList) {
                System.out.println("ID: " + gameStatus.getId() +
                        ", Board: " + gameStatus.getBoard() +
                        ", Next Move: " + gameStatus.getNextPlayer() +
                        ", Blacks Captured: " + gameStatus.getBlacksCaptured() +
                        ", Whites Captured: " + gameStatus.getWhitesCaptured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}