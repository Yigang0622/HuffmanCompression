package it.miketech;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 01/12/2016.
 */
public class FileUtil {

    public static byte[] readFileByBytes(String path) {
        Path mPath = Paths.get(path);
        byte[] data = new byte[0];

        try {
            data = Files.readAllBytes(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


    public static void writeFile(String path,byte[] arr) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(arr);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
