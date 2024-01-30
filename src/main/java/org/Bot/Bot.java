package org.Bot;

import org.gameEngine.*;
import org.models.GameState;
import org.models.Move;
import org.models.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Bot {
    final HashMap<Player, MinMaxStrategy> minMaxStrategies;

    private GameEngine gameEngine;
    private MinMaxStrategy minMaxStrategy;

    {
        minMaxStrategies = new HashMap<>();
        minMaxStrategies.put(Player.BLACK, new MinStrategy());
        minMaxStrategies.put(Player.WHITE, new MaxStrategy());

    }
    public Bot(GameEngine ge){
        this.gameEngine = ge;
    }


    public Move findBestMove(int depth, Player player) {
        List<Move> availableMoves = MoveHelper.getAvailableMoves(gameEngine.board, player);
        Move bestMove = null;
        int bestValue = isMaximizingPlayer(player)? Integer.MIN_VALUE : Integer.MAX_VALUE;
        this.minMaxStrategy = minMaxStrategies.get(player);
        //System.out.println(availableMoves);

        for (Move move : availableMoves) {
            var backUpState = GameState.getDeepCopy(gameEngine.getCurrentState());

            //gameEngine.getCurrentState().printCurrentState();
            gameEngine.makeMove(move);
            //gameEngine.getCurrentState().printCurrentState();
            int value = minimax(depth - 1, player);
            //System.out.println(value);
            gameEngine.setState(backUpState);
           // gameEngine.getCurrentState().printCurrentState();

            //System.out.println(bestValue);
            if(minMaxStrategy.compare(bestValue, value)){
                bestMove = move;
                System.out.println(bestMove);
                bestValue = value;
            }
        }

        return bestMove;
    }

    private int minimax(int depth, Player player) {

        if (depth == 0) {
            return gameEngine.getState();
            //To do implement strategies
        }

        List<Move> availableMoves = MoveHelper.getAvailableMoves(gameEngine.board, player);

        if (isMaximizingPlayer(player)){
            int maxEval = Integer.MIN_VALUE;
            for (Move move : availableMoves) {
                var backUpState = GameState.getDeepCopy(gameEngine.getCurrentState());
                gameEngine.makeMove(move);
                int eval = minimax(depth - 1, Player.getOpponent(player));
                gameEngine.setState(backUpState);
                //gameEngine.getCurrentState().printCurrentState();
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : availableMoves) {
                var backUpState = GameState.getDeepCopy(gameEngine.getCurrentState());
                gameEngine.makeMove(move);
                int eval = minimax(depth - 1, Player.getOpponent(player));
                gameEngine.setState(backUpState);
                //gameEngine.getCurrentState().printCurrentState();
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }
    //White is always maximizing Player
    private boolean isMaximizingPlayer(Player player) {
        return player.equals(Player.WHITE);
    }

}
