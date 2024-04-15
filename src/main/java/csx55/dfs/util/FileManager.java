package csx55.dfs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {

    public static byte[] readFromDisk(String filepath){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filepath));
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeToDisk(String outputFilePath, byte[] bytes){

        File outputFile = new File(outputFilePath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
