package csx55.dfs.wireformats;

import csx55.dfs.util.IpPort;

import java.io.*;

public class CSResponse implements Event{
    int MESSAGE_TYPE = Protocol.CS_RESPONSE;

    IpPort CS;
    int sequence;

    public CSResponse(IpPort cs, int sequence){
        this.CS = cs;
        this.sequence = sequence;
    }

    public CSResponse(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in CSResponse");
        }
        this.sequence = din.readInt();
        this.CS = new IpPort(din);

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

        dout.writeInt(this.sequence);
        CS.marshall(dout);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public IpPort getCS() {
        return CS;
    }

    public int getSequence() {
        return sequence;
    }
}



