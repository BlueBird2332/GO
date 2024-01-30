package org.clientServer.protocols;

import org.models.Player;

import java.io.Serializable;

public class ClientToServerMessage implements Serializable {
    private final Type messageType;
    private int row;
    private int column;
    private Player player;

    public ClientToServerMessage(ClientToServerMessage.Type messageType, int row, int column, Player player){
        this.messageType = messageType;
        this.row = row;
        this.column = column;
        this.player = player;
    }

    public ClientToServerMessage.Type type(){
        return this.messageType;
    }

    public int row(){
        return this.row;
    }
    public int column(){
        return this.column;
    }

    public Player player(){
        return this.player;
    }

    public String body(){
        return this.body();
    }
    public enum Type {
        MAKE_MOVE,
        PASS,
        SURRENDER
    }

}
