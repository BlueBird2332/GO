package org.sampleDatabase;

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

    private static void addMove(String board, String nextMove, int blacksCaptured, int whitesCaptured) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Create a new Move object
            Move move = new Move(board, nextMove, blacksCaptured, whitesCaptured);

            // Save the move to the database
            session.persist(move);

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
            List<Move> moves = session.createQuery("FROM Move").list();

            // Print retrieved moves
            System.out.println("Retrieved Moves:");
            for (Move move : moves) {
                System.out.println("ID: " + move.getId() +
                        ", Board: " + move.getBoard() +
                        ", Next Move: " + move.getNextMove() +
                        ", Blacks Captured: " + move.getBlacksCaptured() +
                        ", Whites Captured: " + move.getWhitesCaptured());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}