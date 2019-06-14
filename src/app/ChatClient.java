package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class ChatClient {
    
    ChatRoom room;
    String name;
    WebSocketConnection connection;

    public void start(String name, IWantNewMessages listener) {
        this.name = name;
        connection = new WebSocketConnection(); 
        connection.connect("ws://192.168.43.163:8085", listener);
    }

    public void sendMessage(String message) {
        connection.sendMessage(message);
    }
}



