package csx55.dfs.replication;

public class ChunkServer {

    // Stores chunks belonging to files on local disk
    // Checksums are adding to the files before they are written to disk
    // REads will also check checksums of the 8KB slices  --> then send chunk to the client


    String STORAGE_PATH = "/tmp/chunk-server/";

//    final private int TOTAL_SPACE_AVAILABLE = GB;
//    int GB  = 10;
//    int MB = 10;

    // list of files with the chunks associated to the files
    // If corruption is detected during reads and writes the controller node is informed





    public ChunkServer(String controllerIp, int controllerPort){

    }
}
