package csx55.dfs.wireformats;

import java.io.*;

public class DownloadResponse implements Event{
    int MESSAGE_TYPE = Protocol.DOWNLOAD_RESPONSE;

    int numberOfChunks;

    public DownloadResponse(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in DownloadRequest");
        }
        this.numberOfChunks = din.readInt();
        baInputStream.close();
        din.close();
    }

    public DownloadResponse(int numberOfChunks){
        this.numberOfChunks = numberOfChunks;
    }

    public int getNumberOfChunks(){
        return this.numberOfChunks;
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

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
