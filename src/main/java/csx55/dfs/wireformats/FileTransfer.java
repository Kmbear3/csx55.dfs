package csx55.dfs.wireformats;

import csx55.dfs.util.IpPort;

import java.io.IOException;

public class FileTransfer implements Event{
    byte[] chunk;
    IpPort[] cs;

    public FileTransfer(IpPort[] chunkServers, byte[] chunk){

    }

    public FileTransfer(byte[] marshalledBytes){

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
