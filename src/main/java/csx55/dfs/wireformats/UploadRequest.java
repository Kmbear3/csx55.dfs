package csx55.dfs.wireformats;

import java.io.*;

public class UploadRequest implements Event {
    int MESSAGE_TYPE = Protocol.UPLOAD_REQUEST;
    String src;
    String dest;

    public UploadRequest(String src, String dest){
        this.src = src;
        this.dest = dest;
    }

    public UploadRequest(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in UploadRequest");
        }

        this.src = WireHelper.unmarshallString(din);
        this.dest = WireHelper.unmarshallString(din);

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

        WireHelper.marshallString(dout, this.src);
        WireHelper.marshallString(dout, this.dest);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getSrc() {
        return src;
    }

    public String getDest() {
        return dest;
    }
}
