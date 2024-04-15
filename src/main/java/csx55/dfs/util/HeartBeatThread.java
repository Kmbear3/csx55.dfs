package csx55.dfs.util;

import csx55.dfs.replication.Chunk;
import csx55.dfs.replication.ChunkServer;
import csx55.dfs.wireformats.HeartBeat;

import java.io.IOException;
import java.util.ArrayList;

public class HeartBeatThread implements Runnable {

    private ChunkServer cs;
    public HeartBeatThread(ChunkServer chunkServer){
        this.cs = chunkServer;
    }

    private void sendBeat(){
        try {
            ArrayList<Chunk> chunks = cs.getChunks();
            int freeSpace = cs.getFreeSpace();
            HeartBeat beat = new HeartBeat(chunks, freeSpace);
            cs.sendToController(beat.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while(true){
            sendBeat();
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
