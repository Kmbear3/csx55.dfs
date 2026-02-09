# README
This project is an implementation of GFS. This project uses fault tolerance by leveraging file chunk replication. The correctness of the chunks is ensured by utilizing checksums created by the SHA-1 message digest algorithm. Reads / Writes are done via interactions with the system entrypoint: the ChunkServer.  

* execute all commands under csx55.overlay/

## to clean 
gradle clean 

## to compile: 
gradle build

## to execute registry:
java -cp ./app/build/libs/app.jar csx55.overlay.node.Registry <registry-port>

## to execute messaging node:
java -cp ./app/build/libs/app.jar csx55.overlay.node.MessagingNode <registry-name> <registry-port>

I have created scripts to execute this project as well:

./node.sh <registry-name> <registry-port>

./registry.sh <registry-port>

