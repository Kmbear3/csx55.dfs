package csx55.dfs.node;

import java.net.Socket;

import csx55.dfs.wireformats.Event;

public interface Node {
    public void onEvent(Event event, Socket socket);
}
