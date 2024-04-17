package csx55.dfs.wireformats;

import csx55.dfs.replication.Chunk;
import csx55.dfs.util.IpPort;

import java.io.*;
import java.util.ArrayList;

public class FileTransfer implements Event{
    byte[] chunk;
    IpPort[] cs;
    int sequenceNumber;
    String destination;
    String fileName;

    int MESSAGE_TYPE = Protocol.FILE_TRANSFER;

    public FileTransfer(IpPort[] chunkServers, byte[] chunk, int sequenceNumber, String destination, String filename){
        this.chunk = chunk;
        this.cs = chunkServers;
        this.sequenceNumber = sequenceNumber;
        this.destination = destination;
        this.fileName = filename;
    }

    public FileTransfer(byte[] marshalledBytes) throws  IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in FileTransfer");
        }

        this.chunk = WireHelper.unmarshallFile(din);
        this.cs = new IpPort[3];

        for(int i = 0; i < cs.length; i ++){
            cs[i] = new IpPort(din);
        }

        this.sequenceNumber = din.readInt();
        this.destination = WireHelper.unmarshallString(din);
        this.fileName = WireHelper.unmarshallString(din);

        baInputStream.close();
        din.close();
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

        WireHelper.marshallFile(dout, this.chunk);

        for(int i = 0; i < cs.length; i ++){
            cs[i].marshall(dout);
        }

        dout.writeInt(sequenceNumber);
        WireHelper.marshallString(dout, destination);
        WireHelper.marshallString(dout, fileName);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public IpPort[] getCs() {
        return cs;
    }

    public String getDestination() {
        return destination;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getFileName() {
        return fileName;
    }
}
