package org.example.clientServer;

import org.example.clientServer.Client;
import org.example.clientServer.protocols.ClientToServerMessage;
import org.example.clientServer.protocols.ServerToClientMessage;
import org.example.game.Board;
import org.example.game.Constants;
import org.example.game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public interface ClientInterface extends Runnable{

    
}
