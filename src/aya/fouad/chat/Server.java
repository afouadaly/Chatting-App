package aya.fouad.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Server {

    public static void main(String args[]) {
        
        new Server(6000).start();
    }
    
    private int port;
    private Map<String, Connection> registery;
    
    private Server(int port) {
        this.port = port;
        registery = new TreeMap<>();
    }
    
    Connection getConnection(String name) {
        return registery.get(name);
    }
    
    boolean addConnection(String name, Connection con) {
        if (registery.containsKey(name)) return false;
        registery.put(name, con);
        return true;
        
    }
    
    void removeConnection(String name) {
        Connection con = registery.get(name);
        if (con != null) {
            con.close();
            registery.remove(name);
        }
    }
    
    Set<String> getClients() {
        return registery.keySet();
    }
    
    void start() {

        System.out.println(">>> Chat Server <<<");

        ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(port);
            
            while (true) {
                ServerConnection con = new ServerConnection(this, serverSocket.accept());
                con.start();
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        
         } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
