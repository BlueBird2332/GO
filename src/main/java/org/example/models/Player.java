package org.example.models;

public enum Player {
    BLACK("B"), WHITE("W"), NOONE("N");
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
        }
        else if(s.equals(WHITE.value)) {
            return WHITE;
        }
        else {
            return NOONE;
        }
    }

}
