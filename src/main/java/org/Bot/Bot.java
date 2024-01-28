package org.Bot;

import org.gameEngine.*;
import org.models.Move;

import java.util.List;

public class Bot {

    public static Move findBestMove(GameEngine game, int depth) {
        List<Move> availableMoves = game.getAvailableMoves();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Move move : availableMoves) {
            game.makeMove(move);
            int value = minimax(game, depth - 1, false);
            game.undoMove(move);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimax(GameEngine game, int depth, boolean isMaximizingPlayer) {
        if (depth == 0) {
            return game.evaluateBoard();
        }

        List<Move> availableMoves = game.getAvailableMoves();

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : availableMoves) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, false);
                game.undoMove(move);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : availableMoves) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, true);
                game.undoMove(move);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
}
}
