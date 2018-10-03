package aya.fouad.chat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) throws UnknownHostException, IOException {
        new Client("localhost", 6000).start();
    }

    private String server;
    private int port;

    private Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    void start() throws UnknownHostException, IOException {

        System.out.println(">>> Chat Client <<<");

        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);
        Connection con = null;
        
        try {
            con = new ClientConnection(new Socket(server, port));
            con.start();
            
            String cmd = null;
            String name = null;
            while (true) {
                
                // read command:
                //System.out.print(name != null ? name + " >" : ">");
                cmd = in.nextLine();

                if (cmd.startsWith("join ")) {
                    name = cmd.substring("join ".length());
                    con.join(name);
                
                } else if (cmd.startsWith("chat ")) {
                    int msgIndex = cmd.indexOf(' ', "chat ".length() + 1);
                    String destination = cmd.substring("chat ".length(), msgIndex);
                    String msg = cmd.substring(msgIndex + 1);
                    con.chat(destination, msg);
                
                } else if ("list".equalsIgnoreCase(cmd)) {
                    con.list();
                
                } else if ("quit".equalsIgnoreCase(cmd) || "bye".equalsIgnoreCase(cmd)) {
                    con.quit();
                }
            }
        
        } finally {
            if (con != null) con.close();
        }
    }
}
