package aya.fouad.chat;

import java.io.IOException;
import java.net.Socket;

class ClientConnection extends Connection {
    
    ClientConnection(Socket connection) throws IOException {
        super(connection);
    }

    @Override
    void onCallBack(String source, String msgBody) throws IOException {
        System.out.println(source + "> " + msgBody);
    }

    @Override
    void onError(String error) throws IOException {
        System.err.println(error);
    }
}
