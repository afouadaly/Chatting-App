package aya.fouad.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class Connection implements Runnable {
    
    private static final char SEPARATOR = ':';
    private static final String JOIN = "Join: ";
    private static final String LIST = "List: ";
    private static final String JOIN_OK = "Join_OK: ";
    private static final String CHAT = "Chat: ";
    private static final String ERROR = "Error: ";
    private static final String CALL_BACK = "Call-Back: ";
    private static final String QUIT = "Quit";

    private Socket connection = null;
    private DataOutputStream out;
    private DataInputStream in;
    private String name;
    private boolean active;
    
    Connection(Socket connection) throws IOException {
        this.connection = connection;
        this.out = new DataOutputStream(connection.getOutputStream());
        this.in = new DataInputStream(connection.getInputStream());
        out.flush(); 
        active = false;        
    }
    
    void start() {
        active = true;

        // start a new thread for each connection
        // when Thread.start is called the Connection.run will be called on that thread.
        new Thread(this).start();    
    }
    
    void stop() {
        active = false;
    }

    void join(String name) throws IOException {
        sendMessage(JOIN + name);
    }

    public void list() throws IOException {
        sendMessage(LIST);
    }

    void joinOk(String name) throws IOException {
        sendMessage(JOIN_OK + name);
    }

    void chat(String destination, String message) throws IOException {
        sendMessage(CHAT + name + SEPARATOR + destination + SEPARATOR + message);
    }

    void callBack(String source, String message) throws IOException {
        sendMessage(CALL_BACK + source + SEPARATOR + message);
    }

    void quit() throws IOException {
        sendMessage(QUIT + name);
        stop();
    }
    
    void sendError(String errMessage) throws IOException {
        sendMessage(ERROR + errMessage);
    }
    
    void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (connection != null) connection.close();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void recieveMessage() throws IOException {
        
        try {
            String message = in.readUTF();
            if (message == null) throw new IllegalArgumentException("Protocol Error: message can't be null");
            
            //System.err.println(">>>>>>> " + message);
            
            if (message.startsWith(JOIN)) {
                String source = message.substring(JOIN.length());
                onJoin(source);
                
            } else if (message.startsWith(JOIN_OK)) {
                String name = message.substring(JOIN_OK.length());
                this.name = name;            
                
            } else if (message.startsWith(LIST)) {
                onList();
                
            } else if (message.startsWith(CHAT)) {
                int sourceTerminator = message.indexOf(SEPARATOR, CHAT.length());
                int destTerminator = message.indexOf(SEPARATOR, sourceTerminator + 1);
                
                String source = message.substring(CHAT.length(), sourceTerminator);
                String destination = message.substring(sourceTerminator + 1, destTerminator);
                String msgBody = message.substring(destTerminator + 1);
                
                onRoute(source, destination, msgBody);
    
    
            } else if (message.startsWith(CALL_BACK)) {
                int destTerminator = message.indexOf(SEPARATOR, CALL_BACK.length());
                
                String destination = message.substring(CALL_BACK.length(), destTerminator);
                String msgBody = message.substring(destTerminator + 1);
    
                onCallBack(destination, msgBody);
                
            } else if (message.startsWith(QUIT)) {
                String source = message.substring(QUIT.length());
                onQuit(source);
                stop();
            
            } else if (message.startsWith(ERROR)) {
                String error = message.substring(ERROR.length());
                onError(error);
            
            } else {
                throw new IllegalArgumentException("Protocol Error: invalid request");
            }
        } catch (EOFException e) {
            stop();
        }
    }

    void onJoin(String source) throws IOException {
    }

    void onQuit(String source) throws IOException {
    }

    void onList() throws IOException {
    }

    void onCallBack(String source, String msgBody) throws IOException {
    }
    
    void onError(String error) throws IOException {
    }

    void onRoute(String source, String destination, String msgBody) throws IOException {
    }
  
    private void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    @Override
    public void run() {
        try {
            while (active) {
                recieveMessage();
            }
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
