package app;


class ChatClient {

    ChatRoom room;
    String name;
    IWantNewMessages newMessageProcessor;

    public void start(String name) {
        room = new ChatRoom(); 
    }

    public void addMessageReceiveListener(IWantNewMessages listener){
        newMessageProcessor = listener;
    }

    public void sendMessage(String message){
        System.out.println("message to send: " + message);
        if (newMessageProcessor != null)
            newMessageProcessor.processNewMessage(message);
    }

}



