package csx55.dfs.replication;

import csx55.dfs.wireformats.Event;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.wireformats.*;

import java.net.Socket;

public class Controller implements Node{

    private final int port;

    // knows where the chunks are in the system
    // This information is built from heartbeats
    // Checks the liveness of the server
        // When it doesn't receive heartbeats
    public Controller(int port){
        System.out.println("Creating Controller, Listening for Connections. Port: " + port);
        this.port = port;
        configureServer(this, port);
    }

    public void configureServer(Node node, int port){
        TCPServerThread tcpServer = new TCPServerThread(this, port);
        Thread serverThread = new Thread(tcpServer);
        serverThread.start();
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            switch(event.getType()){
                case Protocol.REGISTER_REQUEST:

                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
