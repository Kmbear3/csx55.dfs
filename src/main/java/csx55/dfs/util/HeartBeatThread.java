package csx55.dfs.util;

import csx55.dfs.replication.ChunkServer;

public class HeartBeatThread implements Runnable {

    // Major and minor heartbeats

    // Info
        // space available

    private ChunkServer cs;
    public HeartBeatThread(ChunkServer chunkServer){
        this.cs = chunkServer;
    }

    private void sendBeat(){

    }

    @Override
    public void run() {
        while(true){

        }
    }
}
