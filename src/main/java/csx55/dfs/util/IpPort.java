package csx55.dfs.util;

import csx55.dfs.wireformats.WireHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IpPort {
    final public String ip;
    final public int port;

    public IpPort(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public IpPort(DataInputStream din) throws IOException {
       this.ip = WireHelper.unmarshallString(din);
       this.port = din.readInt();
    }

    public void marshall(DataOutputStream dout) throws IOException{
        WireHelper.marshallString(dout, this.ip);
        dout.writeInt(this.port);
    }
}
