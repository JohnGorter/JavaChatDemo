package app;


class MessageResponder implements IWantNewMessages {

    @Override
    public void processNewMessage(
        String message) {
        System.out.println(
            "SERVER SENDS: " + message
        ); 
    }

}