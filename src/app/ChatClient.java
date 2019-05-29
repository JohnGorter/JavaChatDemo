package app;


class ChatClient implements IWantNewMessages {

    ChatRoom room;
    String name;
    IWantNewMessages newMessageProcessor;
    WebSocketConnection connection;

    public void start(String name) {
        this.name = name;
        connection = new WebSocketConnection(); 
        connection.connect("ws://172.16.38.63:8085", this);
    }

    public void addMessageReceiveListener(IWantNewMessages listener){
        newMessageProcessor = listener;
    }

    public void sendMessage(String kamer, String message){
        System.out.println("message to " + kamer + " send: " + message);
        connection.sendMessage(name + " says: " + message);
    }

    @Override
    public void processNewMessage(String message) {
        if (newMessageProcessor != null) 
            newMessageProcessor.processNewMessage(message);
    }

    
}



