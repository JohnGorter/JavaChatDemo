package app;

import java.net.http.*;
import java.net.URI;
import java.util.concurrent.*;

class WebSocketConnection implements WebSocket.Listener {
    WebSocket server;
    IWantNewMessages listener;

    public Boolean connect(String url, IWantNewMessages l){
        listener = l;
        CompletableFuture<WebSocket> server_cf = HttpClient.newHttpClient().newWebSocketBuilder().buildAsync(URI.create(url),  this);
        server = server_cf.join();
        return true;
    }

    public void sendMessage(String message){
        server.sendText(message, true);
    }
    
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        listener.processNewMessage("" + data);
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }
}