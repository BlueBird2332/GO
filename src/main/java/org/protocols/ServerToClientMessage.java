package org.protocols;

public class ServerToClientMessage {

    private final Type messageType;
    private final String messageBody;

    public ServerToClientMessage(Type messageType, String messageBody){
        this.messageType = messageType;
        this.messageBody = messageBody;
    }

    public Type type(){
        return this.messageType;
    }

    public String body(){
        return this.body();
    }
    public enum Type{
        MOVE_MADE,
        MOVE_FAILURE
    }
}
