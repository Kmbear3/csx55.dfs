package csx55.dfs.wireformats;

import csx55.dfs.util.Constants;
import csx55.dfs.util.IpPort;

import java.io.*;

public class ChunkRequest implements Event{
    int MESSAGE_TYPE = Protocol.CHUNK_REQUEST;

    int sequnce;
    String clusterLocationFileName;
    IpPort clientInfo;

    public ChunkRequest(IpPort clientInfo, String clusterLocationFileName, int sequnce) {
        this.clusterLocationFileName = clusterLocationFileName;
        this.sequnce = sequnce;
        this.clientInfo = clientInfo;
    }

    public ChunkRequest(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != MESSAGE_TYPE){
            System.err.println("type mismatch in ChunkRequest");
        }

        this.clusterLocationFileName = WireHelper.unmarshallString(din);
        this.sequnce = din.readInt();
        this.clientInfo = new IpPort(din);

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

        WireHelper.marshallString(dout, this.clusterLocationFileName);
        dout.writeInt(this.sequnce);
        this.clientInfo.marshall(dout);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public String getClusterLocationFileName() {
        return this.clusterLocationFileName;
    }

    public int getSequnce() {
        return sequnce;
    }

    public IpPort getClientInfo(){
        return clientInfo;
    }
}



