package org.example.game;

import java.io.Serializable;
import java.util.Arrays;

public interface BoardInterface extends Serializable {

    String[][] getBoard();
    int getSize();
    String getCellContent(int row, int column);

    void modifyBoard(int row, int column, Constants constant);

}
