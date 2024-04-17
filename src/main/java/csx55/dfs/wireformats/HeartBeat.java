package csx55.dfs.wireformats;

import java.io.IOException;
import java.util.ArrayList;
import csx55.dfs.replication.Chunk;
import csx55.dfs.util.IpPort;

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
    IpPort myInfo;

    public HeartBeat(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in heartbeat");
        }

        this.numberOfChunks = din.readInt();
        this.chunks = new ArrayList<>();
        for(int i = 0; i < numberOfChunks; i++){
            chunks.add(new Chunk(din));
        }

        this.freeSpace = din.readInt();
        String ip = WireHelper.unmarshallString(din);
        int port = din.readInt();

        this.myInfo = new IpPort(ip, port);

        baInputStream.close();
        din.close();
    }

    // Just doing major heartbeats for now
    public HeartBeat(ArrayList<Chunk> chunks, int freeSpace, IpPort myInfo){
        this.chunks = chunks;
        numberOfChunks = chunks.size();
        this.freeSpace = freeSpace;
        this.myInfo = myInfo;
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

        WireHelper.marshallString(dout, myInfo.ip);
        dout.writeInt(myInfo.port);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getID(){
        return myInfo.ip + ":" + myInfo.port;
    }

    public String getIP(){
        return this.myInfo.ip;
    }

    public int getPort(){
        return this.myInfo.port;
    }
    public ArrayList<Chunk> getChunks() {
        return chunks;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    public int getFreeSpace() {
        return freeSpace;
    }

}
