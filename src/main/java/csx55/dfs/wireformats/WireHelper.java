package csx55.dfs.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

abstract public class WireHelper {

    static public void marshallString(DataOutputStream dout, String string) throws IOException{
        byte[] stringBytes = string.getBytes();
        int stringLength = stringBytes.length;
        dout.writeInt(stringLength);
        dout.write(stringBytes);
    }

    static public void marshallFile(DataOutputStream dout, byte[] file) throws IOException{
        int stringLength = file.length;
        dout.writeInt(stringLength);
        dout.write(file);
    }

    static public String unmarshallString(DataInputStream din) throws IOException{
        int stringLength = din.readInt();
        byte[] stringBytes = new byte[stringLength];
        din.readFully(stringBytes);
        return new String(stringBytes);
    }

    static public byte[] unmarshallFile(DataInputStream din) throws IOException{
        byte[] file;
        int fileLength = din.readInt();
        file = new byte[fileLength];
        din.readFully(file);
        return file;
    }
}
