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
import csx55.dfs.wireformats.UploadRequest;
import csx55.dfs.wireformats.UploadResponse;
import csx55.dfs.wireformats.Protocol;

public class Client implements Node{

    TCPSender controllerSender;
    private TCPServerThread server;

    public Client(String controllerIp, int port){
        try {
            Socket registrySocket = new Socket(controllerIp, port);
            this.controllerSender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void configureServer(Node node){
        this.server = new TCPServerThread(node);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            switch(event.getType()){
                case Protocol.UPLOAD_RESPONSE:
                    handleUploadReponse(new UploadResponse(event.getBytes()));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + event.getType());
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        String controllerName = args[0];
        int controllerPort = Integer.parseInt(args[1]);

        Client client = new Client(controllerName, controllerPort);
        CLIHandler cliHandler = new CLIHandler(client);

        while(true){
            cliHandler.readCSInstructions();
        }
    }

    public void uploadFile(String source, String destination) {
        try {
            this.controllerSender.sendData((new UploadRequest(source, destination)).getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleUploadReponse(UploadResponse uploadResponse) {
        // Randomly choose a chunkserver to initiate file transfer to
        // Fuck, this won't work. State needs to be saved in-between the receiving of new chunkservers
        // Build out an object for this to save the intermediary state - file chunks
        // --> UploadManager.java
        //

    }
}
