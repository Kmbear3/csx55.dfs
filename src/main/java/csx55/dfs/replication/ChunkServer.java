package csx55.dfs.replication;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.util.*;
import csx55.dfs.wireformats.*;


public class ChunkServer implements Node{

    TCPSender controllerSender;
    private TCPServerThread server;
    HashMap<String, Chunk> chunks;
    int totalSpace = 1 * Constants.GB;
    int usedSpace = 0;
    public String csIP;
    public int port;
    public IpPort myInfo;

    public ChunkServer(String controllerIp, int controllerPort){
        try {
            this.chunks = new HashMap<>();
            Socket registrySocket = new Socket(controllerIp, controllerPort);
            this.controllerSender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);

            this.csIP = this.server.getIP();
            this.port = this.server.getPort();
            this.myInfo = new IpPort(csIP, port);

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

    public HashMap<String, Chunk> getChunks(){
        return this.chunks;
    }

    public int getFreeSpace(){
        return this.totalSpace - this.usedSpace;
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            switch(event.getType()){
                case Protocol.FILE_TRANSFER:
                    handleChunkUpload(new FileTransfer(event.getBytes()));
                    break;
                case Protocol.CHUNK_REQUEST:
                    handleChunkRequest(new ChunkRequest(event.getBytes()));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + event.getType());
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        }
    }

    synchronized private void handleChunkRequest(ChunkRequest chunkRequest) throws IOException {
        System.out.println(chunkRequest.getClusterLocationFileName());
        byte[] chunk = FileManager.readFromDisk(chunkRequest.getClusterLocationFileName() + "_chunk" + chunkRequest.getSequnce());
        FileChunk fileChunk = new FileChunk(chunk, chunkRequest.getSequnce());

        IpPort client = chunkRequest.getClientInfo();
        client.sendMessage(fileChunk.getBytes());

        System.out.println("Sending chunk... " + chunkRequest.getClusterLocationFileName() + "_chunk" + chunkRequest.getSequnce());
    }

    synchronized private void handleChunkUpload(FileTransfer ft) throws IOException{
        System.out.println("Received file upload: " + ft.getFileName());
        byte[] chunkByte = ft.getChunk();
        this.usedSpace = this.usedSpace + chunkByte.length;

        Chunk chunk = new Chunk(ft.getChunk(), ft.getSequenceNumber(), ft.getFileName(), ft.getDestination());
        chunks.put(chunk.getName(), chunk);
        forwardChunk(ft);
    }

    private void forwardChunk(FileTransfer ft) throws IOException {
        IpPort[] chunkservers = ft.getCs();
        System.out.println("chunkserver size: " + chunkservers.length);
        if(chunkservers.length > 1){
            ArrayList<IpPort> forwards = new ArrayList<>();
            for(int i = 0; i < chunkservers.length; i++){
                chunkservers[i].print();
                if(!chunkservers[i].equals(myInfo)) {
                    forwards.add(chunkservers[i]);
                }
            }
            IpPort[] ftForwards = new IpPort[forwards.size()];
            System.out.println("Made it here");
            for(int i = 0; i < ftForwards.length; i++){
                ftForwards[i] = forwards.get(i);
            }
            FileTransfer fileTransfer = new FileTransfer(ftForwards, ft.getChunk(), ft.getSequenceNumber(), ft.getDestination(), ft.getFileName());
            ftForwards[0].sendMessage(fileTransfer.getBytes());
            System.out.println("Replicating Chunk...");
        }
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
