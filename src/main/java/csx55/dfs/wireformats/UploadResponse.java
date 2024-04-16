package csx55.dfs.wireformats;

import csx55.dfs.util.IpPort;

import java.io.*;
import java.util.ArrayList;

public class UploadResponse implements Event{
    String src;
    String dest;
    final int MESSAGE_TYPE = Protocol.UPLOAD_RESPONSE;
    IpPort[] cs;

    public UploadResponse(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in UploadRequest");
        }

        this.src = WireHelper.unmarshallString(din);
        this.dest = WireHelper.unmarshallString(din);

        this.cs = new IpPort[3];

        for(int i = 0; i < cs.length; i++){
            cs[i] = new IpPort(din);
        }
        baInputStream.close();
        din.close();
    }

    public UploadResponse(String src, String dest, IpPort[] cs){
        this.src = src;
        this.dest = dest;
        this.cs = cs;
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

        WireHelper.marshallString(dout, this.src);
        WireHelper.marshallString(dout, this.dest);

        for(int i = 0; i < cs.length; i ++){
            cs[i].marshall(dout);
        }

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;

    }
}
