package csx55.dfs.util;

import csx55.dfs.replication.ChunkServer;
import csx55.dfs.replication.Controller;

import java.io.IOException;
import java.util.Scanner;

//import csx55.dfs.Discovery;
//import csx55.dfs.Peer;
//import csx55.dfs.wireformats.Deregister;

public class CLIHandler {
    private Scanner scan;
    private Controller controller;
    private ChunkServer cs;
//    private Client client;


    public CLIHandler(Controller controller){
       this.scan = new Scanner(System.in);
       this.controller = controller;
    }

    public CLIHandler(ChunkServer cs){
        this.scan = new Scanner(System.in);
        this.cs = cs;
    }

    public void readControllerInstructions(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        // System.out.println("Instruction: " + result[0]);

//        switch(result[0]){
//            case "peer-nodes":
//                controller.printPeerNodes();
//                break;
//            default:
//                System.out.println("Incorrect Instruction. Please try again.");
//        }
    }

    public void readCSInstructions(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        switch(result[0]) {
            case "exit":
        }
    }
//
//    private void sendDeregisterRequest(int status){
//        try {
//
//            Deregister deregister = new Deregister(node.getMessagingNodeIP(), node.getMessagingNodePort(), status);
//            node.sendRegistryMessage(deregister);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
}