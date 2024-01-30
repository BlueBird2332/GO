package org.models;

import java.util.List;
import java.util.Objects;

public record Stone(int row, int column, String contents) {
    public static boolean hasLiberties(List<Stone> neighbours){
        return neighbours.stream().anyMatch(stone -> Objects.equals(stone.contents(), CellContents.EMPTY.value()));
    }

    public static String getOpponentColour(String colour){
        if (colour.equals(CellContents.BLACK.value())){
            return CellContents.WHITE.value();
        }
        if (colour.equals(CellContents.WHITE.value())){
            return CellContents.BLACK.value();
        }
        throw new IllegalArgumentException("Invalid Colour");
    }

}
