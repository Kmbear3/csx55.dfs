package csx55.dfs.wireformats;

import java.io.*;

public class CSRequest implements Event{
    int MESSAGE_TYPE = Protocol.CS_REQUEST;

    String filePath;
    int sequence;

    public CSRequest(String filePath, int sequence){
        this.filePath = filePath;
        this.sequence = sequence;
    }

    public CSRequest(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in CSRequest");
        }
        this.filePath = WireHelper.unmarshallString(din);
        this.sequence = din.readInt();

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

        WireHelper.marshallString(dout, this.filePath);
        dout.writeInt(this.sequence);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getClusterLocationFileName() {
        return this.filePath;
    }

    public int getSequence() {
        return sequence;
    }
}



