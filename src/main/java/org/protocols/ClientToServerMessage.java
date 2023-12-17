package org.protocols;

public class ClientToServerMessage {
    private Type messageType;
    private String messageBody;

    public ClientToServerMessage(ClientToServerMessage.Type messageType, String messageBody){
        this.messageType = messageType;
        this.messageBody = messageBody;
    }

    public ClientToServerMessage.Type type(){
        return this.messageType;
    }

    public String body(){
        return this.body();
    }
    public enum Type {
        MAKE_MOVE,
        SUGGEST_DRAW,
    }

}
