package csx55.dfs.util;

import csx55.dfs.transport.TCPSender;
import csx55.dfs.wireformats.WireHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

    public void sendMessage(byte[] bytes) throws IOException {
        Socket registrySocket = new Socket(this.ip, this.port);
        TCPSender sender = new TCPSender(registrySocket);
        sender.sendData(bytes);
    }

    public boolean equals(IpPort other){
        return other.ip.equals(ip) && other.port == port;
    }

    public void print(){
        System.out.println("IP: " + ip);
        System.out.println("Port: " + port);

    }
}
