package csx55.dfs.wireformats;

import csx55.dfs.util.IpPort;

import java.io.IOException;
import java.util.ArrayList;

public class UploadResponse implements Event{
    String src;
    String dest;
    final int MESSAGE_TYPE = Protocol.UPLOAD_RESPONSE;
    IpPort[] cs;

    public UploadResponse(byte[] marsalledBytes){

    }

    public UploadResponse(String src, String dest, IpPort[] cs){

    }


    @Override
    public int getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return new byte[0];
    }
}
