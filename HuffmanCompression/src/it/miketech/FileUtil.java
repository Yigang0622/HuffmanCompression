package it.miketech;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 01/12/2016.
 */
public class FileUtil {

    public static int[] readFileByBytes(String fileName) {
        File file = new File(fileName);

        List<Integer> bytesArr = new ArrayList();


        InputStream in = null;
        try {
            in = new FileInputStream(file);
            int tempbyte;
            while ((tempbyte = in.read()) != -1) {
                bytesArr.add(tempbyte);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] a = new int[bytesArr.size()];

        for (int i=0;i<bytesArr.size();i++){
            a[i] = bytesArr.get(i);
        }

        return a;
    }

    public static void main(String[] args) throws IOException {
        readFileByBytes("/Users/Mike/Desktop/1.txt");


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
