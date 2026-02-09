# README
This project is an implementation of GFS (Google File System), without Erasure, but with replication. This project uses fault tolerance by leveraging file chunk replication. The correctness of the chunks is ensured by utilizing checksums created by the SHA-1 message digest algorithm. 


* execute all commands under csx55.dfs/

## to clean 
gradle clean 

## to compile: 
gradle build


### To Use Project:

There are 3 components to this project:
  - ChunkServer - `cs.sh`
    This is the entity that is responsible for managing file chunks. There will be N Chunk Servers, where N is equal to the number of machines availble to store files. Typically, this number is equal to 10.
  - Controller Node - `./cont.sh` 
    This is the entity that has knowledge about *where* chunks are stored across the system and the health of the ChunkServers. There is only one instance of the Controller node. The Controller is provided information about the system via Heartbeats from the ChunkServers.
  - Client - this is the entity that uploads files and requests files from the system. The client is responsible for file splitting and assembly.

