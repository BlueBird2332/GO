package org.example;

public enum Constants {
    WHITE("W"),
    BLACK("B"),
    EMPTY("0");
    private String value;
    private int color;
    Constants(String value){
        this.value = value;
    }
    public String value(){
        return this.value;
    }
}
