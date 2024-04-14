package csx55.dfs.replication;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Chunk {
    // total size of chunk
    // Metadata
    // Versioning number
    // Time stamp (Last updated)
    // List of checksums for each 8kb slices of the chunk
   // MessageDigest.getInstance("SHA-1"); I
    // byte[] = 64KB


    public void marshall(DataOutputStream dout){
        
    }

    public void unmarshall(DataInputStream din){

    }
}
