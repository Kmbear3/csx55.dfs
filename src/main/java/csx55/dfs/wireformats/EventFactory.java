package csx55.dfs.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {
    public static Event getEvent(byte[] marshalledBytes){
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        try {
            int messageType = din.readInt();
            switch(messageType){
                default:
                    System.err.println("Didn't have an event!" + messageType);
                    return null;

            }
        }  catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}