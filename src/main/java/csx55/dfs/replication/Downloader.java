package csx55.dfs.replication;

import csx55.dfs.util.Constants;
import csx55.dfs.util.FileManager;
import csx55.dfs.wireformats.FileChunk;

import java.util.HashMap;

public class Downloader implements Runnable {

    int numberOfChunks;
    volatile int receivedChunks;
    HashMap<Integer, byte[]> unassembledFile;
    String outputPath;

    public Downloader(int numberOfChunks, String outputPath){
        this.numberOfChunks = numberOfChunks;
        unassembledFile = new HashMap<>();
        this.outputPath = outputPath;
    }

    public void receiveChunk(FileChunk fileChunk) {
        unassembledFile.put(fileChunk.getSequnce(), fileChunk.getChunk());
        receivedChunks++;
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
