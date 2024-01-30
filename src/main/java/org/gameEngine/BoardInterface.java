package org.gameEngine;

import org.models.Constants;

import java.io.Serializable;

public interface BoardInterface extends Serializable {

    String[][] getBoard();
    int getSize();
    String getCellContent(int row, int column);

    void modifyBoard(int row, int column, Constants constant);

}
