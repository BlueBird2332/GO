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
}
