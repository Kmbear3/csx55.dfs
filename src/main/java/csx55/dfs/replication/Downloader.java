package csx55.dfs.replication;

import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.Constants;
import csx55.dfs.util.FileManager;
import csx55.dfs.util.IpPort;
import csx55.dfs.wireformats.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class Downloader implements Runnable {

    HashMap<Integer, byte[]> unassembledFile;
    String outputPath;
    String clusterFileLocation;
    TCPSender controllerSender;
    int receivedChunks;
    IpPort myInfo;
    String filename;

    public Downloader(IpPort myInfo, TCPSender controllerSender, String source, String outputPath){
        unassembledFile = new HashMap<>();
        this.outputPath = outputPath;
        this.clusterFileLocation = source;
        this.controllerSender = controllerSender;
        this.receivedChunks = 0;
        this.myInfo = myInfo;

        String[] files = source.split("/");
        this.filename = files[files.length -1];

        sendCSRequest(clusterFileLocation, receivedChunks);
    }

    public void receiveChunk(FileChunk fileChunk) {
        unassembledFile.put(fileChunk.getSequnce(), fileChunk.getChunk());
        receivedChunks++;
//        System.out.println("Received Chunk... " + receivedChunks);
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

    public void receiveCSResponse(CSResponse csResponse){
        if(csResponse.EOF() != true) {
            IpPort CS = csResponse.getCS();
            try {
                CS.sendMessage(new ChunkRequest(myInfo, clusterFileLocation, receivedChunks).getBytes());
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            downloadFile();
        }
    }

    private void downloadFile(){
        byte[] file = new byte[receivedChunks * 64 * Constants.KB];

        for(int i = 0; i < receivedChunks; i++){
            byte[] chunk = unassembledFile.get(i);
//            System.out.println("Downloading chunk size: " + chunk.length);
            for(int j = 0; j < chunk.length; j++){
                file[j + (i * 64 * Constants.KB)] = chunk[j];
            }
        }

        // Try to deal with downloading issues
        int i;
        for(i = file.length - 1; i >= 0 && file[i] == 0; i --){}
        if(i != file.length - 1){
            file = Arrays.copyOfRange(file, 0 , i + 1);
        }

        String userDirectory = System.getProperty("user.dir");
        String storageLocation = userDirectory  + outputPath + "/";
        Path path = Paths.get(storageLocation);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Output file path: " + storageLocation);
        FileManager.writeToDisk(storageLocation+filename, file);
    }

    @Override
    public void run() {
        while(true){

        }
    }
}
