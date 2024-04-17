package csx55.dfs.wireformats;

import csx55.dfs.util.Constants;

import java.io.*;

public class FileChunk implements Event{
    int MESSAGE_TYPE = Protocol.FILE_CHUNK;

    int sequnce;
    byte[] chunk;

    public FileChunk(byte[] chunk, int sequnce) {
        this.chunk = chunk;
        this.sequnce = sequnce;
    }

    public FileChunk(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in FileChunk");
        }
        this.chunk = new byte[64 * Constants.KB];

        this.chunk = WireHelper.unmarshallFile(din);
        this.sequnce = din.readInt();

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
        dout.writeInt(this.sequnce);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public int getSequnce() {
        return sequnce;
    }
}



