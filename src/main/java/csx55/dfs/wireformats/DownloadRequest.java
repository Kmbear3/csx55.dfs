package csx55.dfs.wireformats;

import java.io.*;

public class DownloadRequest implements Event{
    int MESSAGE_TYPE = Protocol.DOWNLOAD_REQUEST;

    String filePath;

    public DownloadRequest(String filePath){
        this.filePath = filePath;
    }

    public DownloadRequest(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in DownloadRequest");
        }
        this.filePath = WireHelper.unmarshallString(din);
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

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getFilePath() {
        return this.filePath;
    }
}



