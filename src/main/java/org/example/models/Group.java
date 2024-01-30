package org.example.models;

import org.example.gameEngine.Board;
import org.example.gameEngine.MoveHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public record Group(List<Stone> stones) {
    public static boolean hasBreaths(Group group, Board board){
        for(Stone stone : group.stones()){
            if (hasBreath(stone, board)){
                return true;
            }
        }
        return false;
    };

    public static int groupBreathCount(Group group, Board board){
        int count = 0;
        for(Stone stone : group.stones()){
            count += breathCount(stone, board);
        }
        return count;
    }
    public static int breathCount(Stone stone, Board board) {
        int count = 0;
        for(Stone neighbour : MoveHelper.getNeighbours(stone.row(), stone.column(), board)){
            if(neighbour.contents().equals(CellContents.EMPTY.value())){
                count += 1;
            }
        }
        return count;
    }

    public static boolean hasBreath(Stone stone, Board board){
        return breathCount(stone, board) != 0;
    }

    public static List<Stone> getGroup(Stone stone, Board board) {
        List<Stone> group = new ArrayList<>();
        Set<Stone> visited = new HashSet<>(); // To keep track of visited stones

        // Depth-first search to find connected stones
        dfsSearch(stone, board, group, visited);

        return group;
    }

    private static void dfsSearch(Stone currentStone, Board board, List<Stone> group, Set<Stone> visited) {
        // Mark the current stone as visited
        visited.add(currentStone);

        // Add the current stone to the group
        group.add(currentStone);

        // Check neighbors
        for (Stone neighbor : MoveHelper.getNeighbours(currentStone.row(), currentStone.column(), board)) {
            if (!visited.contains(neighbor) && neighbor.contents().equals(currentStone.contents())) {
                // Recursively explore unvisited neighbors with the same contents
                System.out.println(neighbor);
                dfsSearch(neighbor, board, group, visited);
            }
        }
    }
}

