package aya.fouad.chat;

import java.io.IOException;
import java.net.Socket;

class ServerConnection extends Connection {
    
    private final Server server;
    
    ServerConnection(Server server, Socket connection) throws IOException {
        super(connection);
        this.server = server;
    }

    @Override
    void onJoin(String client) throws IOException {
        if (server.addConnection(client, this)) {
            joinOk(client);
        } else {
            sendError(client + " already logged in.");
        }
    }

    @Override
    void onQuit(String source) throws IOException {
        server.removeConnection(source);
    }
    
    @Override
    void onList() throws IOException {
        StringBuilder list = new StringBuilder();
        int i = 0; // for each client add first then append comma then next
        for (String client: server.getClients()) {
            if (i++ > 0) list.append(", ");
            list.append(client);
        }
        callBack("Server", list.toString());
    }
    
    @Override
    void onRoute(String source, String destination, String msgBody) throws IOException {
        
        Connection destCon = server.getConnection(destination);
        if (destCon != null) {
            destCon.callBack(source, msgBody);
        } else {        
            sendError(destination + " was not found");
        }
    }
}
