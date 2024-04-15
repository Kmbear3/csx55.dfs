package csx55.dfs.replication;

import csx55.dfs.util.CLIHandler;
import csx55.dfs.wireformats.Event;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.wireformats.*;

import java.net.Socket;

public class Controller implements Node {

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
                case Protocol.HEART_BEAT:
                    HeartBeat beat = new HeartBeat(event.getBytes());
                    System.out.println("Received beat");
                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Controller controller = new Controller(port);

        CLIHandler cli = new CLIHandler(controller);
        while(true){
            cli.readControllerInstructions();
        }
    }

}
