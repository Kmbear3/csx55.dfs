package csx55.dfs.replication;

import csx55.dfs.util.Constants;
import csx55.dfs.util.FileManager;
import csx55.dfs.wireformats.WireHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Chunk {

    private int version;
    private String timestamp;
    private int sequenceNum;
    ArrayList<String> checksums;
    private final String storagePath = "/tmp/chunk-server/";
    private String filename;

    public Chunk(byte[] data, int sequence, String filename){
        this.sequenceNum = sequence;
        this.timestamp = LocalDateTime.now().toString();
        this.version = 0;
        this.filename = filename + "_chunk" + sequence;
        checksums = createChecksums(data);
    }

    // Metadata chunk
    public Chunk(int sequenceNum, String timestamp, int version, String filename){
        this.sequenceNum = sequenceNum;
        this.timestamp = timestamp;
        this.version = version;
        this.filename = filename;
    }

    // Test this
    public ArrayList<String> createChecksums(byte[] data){
        ArrayList<String> checksums = new ArrayList<>();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for(int i = 0; i < data.length; i = i + 8* Constants.KB){
                byte[] slice = new byte[8 * Constants.KB];
                for(int j = 0; j < 8 * Constants.KB; j++) {
                    slice[j] = data[i + j];
                }

                String checksum = new String(md.digest(slice));
                System.out.println(checksum);
                checksums.add(checksum);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return checksums;
    }


    // Just the chunk metadata, not the ACTUAL chunk

    public void marshall(DataOutputStream dout) throws IOException {
        dout.writeInt(sequenceNum);
        WireHelper.marshallString(dout, timestamp);
        dout.writeInt(version);
        WireHelper.marshallString(dout, filename);
    }

    public Chunk(DataInputStream din) throws IOException {
        this.sequenceNum = din.readInt();
        this.timestamp = WireHelper.unmarshallString(din);
        this.version = din.readInt();
        this.filename = WireHelper.unmarshallString(din);
    }
}
