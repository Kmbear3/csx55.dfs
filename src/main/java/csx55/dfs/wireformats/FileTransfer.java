package csx55.dfs.wireformats;

import csx55.dfs.util.IpPort;

import java.io.IOException;

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

    public FileTransfer(byte[] marshalledBytes){

    }


    @Override
    public int getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
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
