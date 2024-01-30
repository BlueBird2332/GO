package org.example.models;

public enum CellContents {
    WHITE("W"),
    BLACK("B"),
    EMPTY("0"),
    VISITED("V");
    private String value;
    CellContents(String value){
        this.value = value;
    }
    public String value(){
        return this.value;
    }
}
