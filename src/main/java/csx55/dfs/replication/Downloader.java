package csx55.dfs.replication;

import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.Constants;
import csx55.dfs.util.FileManager;
import csx55.dfs.wireformats.CSRequest;
import csx55.dfs.wireformats.DownloadRequest;
import csx55.dfs.wireformats.DownloadResponse;
import csx55.dfs.wireformats.FileChunk;

import java.io.IOException;
import java.util.HashMap;

public class Downloader implements Runnable {

    int numberOfChunks;
    volatile int receivedChunks;
    HashMap<Integer, byte[]> unassembledFile;
    String outputPath;
    String clusterFileLocation;
    TCPSender controllerSender;

    public Downloader(TCPSender controllerSender, String source, String outputPath){
        unassembledFile = new HashMap<>();
        this.outputPath = outputPath;
        this.receivedChunks = 0;
        this.clusterFileLocation = source;
        this.controllerSender = controllerSender;
        DownloadRequest request = new DownloadRequest(source, 0);

        try {
            controllerSender.sendData(request.getBytes());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void receiveChunk(FileChunk fileChunk) {
        unassembledFile.put(fileChunk.getSequnce(), fileChunk.getChunk());
        receivedChunks++;
    }

    public void handleDownloadResponse(DownloadResponse downloadResponse) {
        this.numberOfChunks = downloadResponse.getNumberOfChunks();
        sendCSRequest(clusterFileLocation, receivedChunks);
    }

    private void sendCSRequest(String clusterFileLocation, int receivedChunks) {
        CSRequest request = new CSRequest(clusterFileLocation, receivedChunks);
        try {
            controllerSender.sendData(request.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        while(numberOfChunks > receivedChunks){

        }

        byte[] file = new byte[numberOfChunks * 64 * Constants.KB];

        for(int i = 0; i < numberOfChunks; i++){
            byte[] chunk = unassembledFile.get(i);
            for(int j = 0; j < chunk.length; j++){
                file[j+i] = chunk[j];
            }
        }

        String userDirectory = System.getProperty("user.dir");
        FileManager.writeToDisk(userDirectory+outputPath, file);
    }
}
