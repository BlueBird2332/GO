package org.models;

public enum Player {
    BLACK("B"), WHITE("W");
    private String value;
    Player(String value){
        this.value = value;
    }
    public String value(){
        return this.value;
    }
    public static Player getOpponent(Player p){
        return p == BLACK? WHITE : BLACK;
    }

    public static Player getPlayerFromColour(String s) {
        if (s.equals(BLACK.value)) {
            return BLACK;
        } else {
            return WHITE;
        }
    }
}
