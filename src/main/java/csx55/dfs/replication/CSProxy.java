package csx55.dfs.replication;

import csx55.dfs.wireformats.HeartBeat;

import java.util.ArrayList;

public class CSProxy {
//    ArrayList<Metadata> metadata;
//    ArrayList<Metadata> metadata;

    String IP;
    int port;

    public CSProxy(HeartBeat beat) {
    }

    public void noHeartBeatAlert(){
        // Perchance create an event when this proxy hasn't been heard of in the correct amount of time
//        Create an Event and replicate chunks onto another machine
    }

    public void update(HeartBeat beat) {
    }
}
