package csx55.dfs.replication;

import csx55.dfs.util.IpPort;
import csx55.dfs.wireformats.HeartBeat;

import java.util.ArrayList;

public class CSProxy {
    String IP;
    int port;
    int freeSpace;
    int totalChunks;

    ArrayList<Chunk> chunks;

    public CSProxy(HeartBeat beat) {
        this.IP = beat.getIP();
        this.port = beat.getPort();
        this.chunks = beat.getChunks();
        this.freeSpace = beat.getFreeSpace();
        this.totalChunks = beat.getNumberOfChunks();
    }

    public void noHeartBeatAlert(){
        // Perchance create an event when this proxy hasn't been heard of in the correct amount of time
//        Create an Event and replicate chunks onto another machine
    }

    public void update(HeartBeat beat) {
        chunks.addAll(beat.getChunks());
        freeSpace = beat.getFreeSpace();
        totalChunks = chunks.size();
    }

    public void printMetaData(){
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.println("Chunks Server - " + IP + ":" + port);
        System.out.println("Number of Chunks: " + totalChunks);
        System.out.println("FreeSpace: " + freeSpace);
        System.out.println("------------------------------------------------  Chunk MetaData  -----------------------------------------------");
        System.out.println(String.format("| %-10s | %28s | %10s | %37s |", "Sequence" ,"TimeStamp","Version", "Filename"));
        for(Chunk chunk : chunks){
            chunk.printMetaData();
        }
    }

    public IpPort getIpPort(){
        return new IpPort(IP, port);
    }

    public boolean hasFile(String filename){
        for(Chunk chunk : chunks){
            if(chunk.getName().equals(filename)){
                return true;
            }
        }
        return false;
    }
}
