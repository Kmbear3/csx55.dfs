package csx55.dfs.wireformats;

import java.io.IOException;
import java.util.ArrayList;
import csx55.dfs.replication.Chunk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class HeartBeat implements Event{
    ArrayList<Chunk> chunks;
    int numberOfChunks;
    int freeSpace;
    final int MESSAGE_TYPE = Protocol.HEART_BEAT;

    public HeartBeat(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in heartbeat");
        }

        this.numberOfChunks = din.readInt();
        ArrayList<Chunk> chunks = new ArrayList<>();
        for(int i = 0; i < numberOfChunks; i++){
            chunks.add(new Chunk(din));
        }

        this.freeSpace = din.readInt();

        baInputStream.close();
        din.close();
    }

    // Just doing major heartbeats for now
    public HeartBeat(ArrayList<Chunk> chunks, int freeSpace){
        this.chunks = chunks;
        numberOfChunks = chunks.size();
        this.freeSpace = freeSpace;
    }

    @Override
    public int getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(this.MESSAGE_TYPE);

        dout.writeInt(this.numberOfChunks);

        for(int i = 0; i < this.numberOfChunks; i ++ ){
            chunks.get(i).marshall(dout);
        }

        dout.writeInt(this.freeSpace);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
