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

public class Client implements Node{

    TCPSender controllerSender;
    private TCPServerThread server;

    // Uploading variables:
    byte[] file = null;
    int sequenceNumber = 0;
    String filename = "";
    long transferredSize = 0;

    // Downloading Variables:
    Downloader downloader;
    IpPort me;

    public Client(String controllerIp, int port){
        try {
            Socket registrySocket = new Socket(controllerIp, port);
            this.controllerSender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);

            String ip = this.server.getIP();
            int myPort = this.server.getPort();
            this.me = new IpPort(ip, myPort);

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
                    handleUploadResponse(new UploadResponse(event.getBytes()));
                    break;
                case Protocol.CS_RESPONSE:
                    downloader.receiveCSResponse(new CSResponse(event.getBytes()));
                    break;
                case Protocol.FILE_CHUNK:
                    System.out.println("Receiving File chunk");
                    downloader.receiveChunk(new FileChunk(event.getBytes()));
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
        System.out.println("Client Starting...");
        String controllerName = args[0];
        int controllerPort = Integer.parseInt(args[1]);

        Client client = new Client(controllerName, controllerPort);
        CLIHandler cliHandler = new CLIHandler(client);

        while(true){
            cliHandler.readClientInstructions();
        }
    }

    synchronized public void uploadFile(String source, String destination) {
        try {
            transferredSize = 0;
            file = FileManager.readFromDisk(source);
            sequenceNumber = 0;
            String[] inputPath = source.split("/");
            filename = inputPath[inputPath.length - 1];
            System.out.println("Uploading file: " + filename);

            this.controllerSender.sendData((new UploadRequest(source, destination)).getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    synchronized private void handleUploadResponse(UploadResponse uploadResponse) {
        IpPort[] chunkservers = uploadResponse.getCs();
        byte[] chunk = new byte[64 * Constants.KB];

        // Le jank -- NOTICE -- if file values are broken, check here!!!!!

        System.out.println("File length: " + file.length);
        int i = 0;
        while(i < chunk.length && (i + (sequenceNumber * 64 * Constants.KB)) < file.length){
            chunk[i] = file[(i + (sequenceNumber * 64 * Constants.KB))];
            i++;
            transferredSize++;
        }
        if(i != chunk.length){
            while(i < chunk.length){
                chunk[i] = 0;
                i++;
                transferredSize++;
            }
        }
        // Infinite loop

        System.out.println("Sending chunk: " + sequenceNumber);
        try {
            chunkservers[0].sendMessage(new FileTransfer(chunkservers, chunk, sequenceNumber, uploadResponse.getDest(), filename).getBytes());

            // There is still data needing to be transmitted, request new chunkServers
            if(!(transferredSize >= file.length)){
                this.controllerSender.sendData((new UploadRequest(uploadResponse.getSrc(), uploadResponse.getDest())).getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sequenceNumber++;
    }

    public void downloadFile(String source, String destination) {
        downloader = new Downloader(me, controllerSender, source, destination);
        Thread downloadThread = new Thread(downloader);
        downloadThread.start();
    }
}
