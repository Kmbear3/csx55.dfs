package csx55.dfs.replication;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.util.CLIHandler;
import csx55.dfs.util.Constants;
import csx55.dfs.util.HeartBeatThread;
import csx55.dfs.wireformats.Event;


public class ChunkServer implements Node{

    // Stores chunks belonging to files on local disk
    // Checksums are adding to the files before they are written to disk
    // REads will also check checksums of the 8KB slices  --> then send chunk to the client

//    final private int TOTAL_SPACE_AVAILABLE = GB;
//    int GB  = 10;
//    int MB = 10;

    // list of files with the chunks associated to the files
    // If corruption is detected during reads and writes the controller node is informed

    String STORAGE_PATH = "/tmp/chunk-server/";
    TCPSender controllerSender;
    private TCPServerThread server;
    ArrayList<Chunk> chunks;
    int totalSpace = 1 * Constants.GB;
    int usedSpace = 0;

    public ChunkServer(String controllerIp, int controllerPort){
        try {
            this.chunks = new ArrayList<>();
            Socket registrySocket = new Socket(controllerIp, controllerPort);
            this.controllerSender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);
            startHeartBeats();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configureServer(Node node){
        this.server = new TCPServerThread(node);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public void startHeartBeats(){
        HeartBeatThread heart = new HeartBeatThread(this);
        Thread heartThread = new Thread(heart);
        heartThread.start();
    }

    public ArrayList<Chunk> getChunks(){
        return this.chunks;
    }

    public int getFreeSpace(){
        return this.totalSpace - this.usedSpace;
    }

    @Override
    public void onEvent(Event event, Socket socket) {
//        try {
//            switch(event.getType()){
//
//
//            }
//        } catch (IOException e) {
//            System.err.println("Error: MessagingNode.onEvent()");
//            e.printStackTrace();
//        }
    }

    public void sendToController(byte[] bytes) throws IOException{
        this.controllerSender.sendData(bytes);
    }

    public static void main(String[] args){
        String controllerName = args[0];
        int controllerPort = Integer.parseInt(args[1]);

        ChunkServer cs = new ChunkServer(controllerName, controllerPort);

        CLIHandler cliHandler = new CLIHandler(cs);

        while(true){
            cliHandler.readCSInstructions();
        }
    }
}
