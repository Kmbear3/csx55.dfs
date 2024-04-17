package csx55.dfs.replication;

import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.CLIHandler;
import csx55.dfs.util.IpPort;
import csx55.dfs.wireformats.Event;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Node {

    private final int port;
    private ConcurrentHashMap<String, CSProxy> csProxies = new ConcurrentHashMap<>();

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
                    handleHeartBeat(beat);
                    System.out.println("Received beat");
                    break;
                case Protocol.UPLOAD_REQUEST:
                    handleUploadRequest(new UploadRequest(event.getBytes()), socket);
                    break;
                default:
                    System.out.println("Protocol Unmatched! " + event.getType());
                    System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleHeartBeat(HeartBeat beat) {
        if(csProxies.contains(beat.getID())){
            CSProxy csProxy = csProxies.get(beat.getID());
            csProxy.update(beat);
        }else{
            CSProxy csProxy = new CSProxy(beat);
            csProxies.put(beat.getID(), csProxy);
        }
    }

    synchronized private void handleUploadRequest(UploadRequest request, Socket socket) throws IOException {
        IpPort[] randomCS = new IpPort[3];
        ArrayList<CSProxy> proxies = new ArrayList<>(csProxies.values());
        Random rand = new Random();

        for(int i = 0; i < 3; i++){
            randomCS[i] = proxies.get(rand.nextInt(proxies.size())).getIpPort();
        }

        UploadResponse response = new UploadResponse(request.getSrc(), request.getDest(), randomCS);
        TCPSender send = new TCPSender(socket);
        send.sendData(response.getBytes());
    }

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Controller controller = new Controller(port);

        CLIHandler cli = new CLIHandler(controller);
        while(true){
            cli.readControllerInstructions();
        }
    }

    public void printAllProxies() {
        for(CSProxy proxy : csProxies.values()){
            proxy.printMetaData();
        }
    }
}
