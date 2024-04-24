package csx55.dfs.replication;

import csx55.dfs.util.Constants;
import csx55.dfs.util.FileManager;
import csx55.dfs.wireformats.WireHelper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;

public class Chunk {

    private int version;
    private String timestamp;
    private int sequenceNum;
    ArrayList<String> checksums;
    private final String storagePath = "/tmp/chunk-server/";
    private String filename;

    public Chunk(byte[] data, int sequence, String filename, String destinationPath){
        this.sequenceNum = sequence;
        this.timestamp = LocalDateTime.now().toString();
        this.version = 0;
        this.filename = filename + "_chunk" + sequence;
        checksums = createChecksums(data);
        String outputPath = storagePath + destinationPath + this.filename;
        try {
            // Create directory - mayhaps jank
            String storageLocation = storagePath + destinationPath + "/";
            Path path = Paths.get(storageLocation);
            Files.createDirectories(path);

        } catch (IOException e) {
            System.err.println("Failed to create directory!");
            e.printStackTrace();
        }
        System.out.println("Storing file: " + outputPath);
        FileManager.writeToDisk(outputPath, data);
        this.filename = outputPath;
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

    public void printMetaData(){
        System.out.println(String.format("| %-10s | %28s | %10s | %37s |", sequenceNum, timestamp, version, filename));
    }

    public String getName(){
        // Include chunk information
        return filename;
    }

    public String getFilename(){
        // Just the file name
        int index = filename.lastIndexOf("_");
        return filename.substring(0, index);
    }

    public boolean correctChecksum(String chunkName, byte[] chunkBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            int arrayIndex = 0;
            for(int i = 0; i < chunkBytes.length; i = i + 8 * Constants.KB){
                byte[] slice = new byte[8 * Constants.KB];
                for(int j = 0; j < 8 * Constants.KB; j++) {
                    slice[j] = chunkBytes[i + j];
                }

//                System.out.println("Comparing Chunks!");
                String readChunkChecksum = new String(md.digest(slice));
                if(!checksums.get(arrayIndex).equals(readChunkChecksum)){
                    System.out.println("\n\n---------------------------------------------------------------------------------------- \n\n");
                    System.out.println("Error Reading Chunk: " + chunkName + " File Corrupted at Slice Number: " + arrayIndex + "\n\n");
                    System.out.println("---------------------------------------------------------------------------------------- \n\n");
                    return false;
                }
                arrayIndex++;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


//    public boolean correctChecksum(String chunkName, byte[] chunkBytes) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//
//            for(int i = 0; i < checksums.size() - 1; i = i + 8 * Constants.KB + 1){
//                byte[] slice = new byte[8 * Constants.KB];
//                slice = Arrays.copyOfRange(chunkBytes, i , i + 8 * Constants.KB );
//
//
//                System.out.println("Comparing Chunks!");
//                String readChunkChecksum = new String(md.digest(slice));
//                if(!checksums.get(i).equals(readChunkChecksum)){
//                    System.out.println("\n\n---------------------------------------------------------------------------------------- \n\n");
//                    System.out.println("Error Reading Chunk: " + chunkName + " File Corrupted at Slice Number: " + i + "\n\n");
//                    System.out.println("---------------------------------------------------------------------------------------- \n\n");
//                    return false;
//                }
//            }
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        return true;
//    }
}
