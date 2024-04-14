package csx55.dfs.wireformats;

import java.io.IOException;
import java.util.ArrayList;
import csx55.dfs.replication.Chunk;


public class HeartBeat implements Event{
    ArrayList<Chunk> chunks;
    int numberOfChunks;
    int freeSpace;

    public HeartBeat(byte[] marshalledBytes) {

    }

    // Just doing major heartbeats for now
    public HeartBeat(ArrayList<Chunk> chunks, int freeSpace){
        this.chunks = chunks;
        numberOfChunks = chunks.size();
        this.freeSpace = freeSpace;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }
}
