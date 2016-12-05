package it.miketech;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        String command = args[0];
        String path = args[1];

        if (command.equals("compress")){
            Compressor compressor = new Compressor(path);
            compressor.compress();
        }else if (command.equals("decompress")){
            Decompressor decompressor = new Decompressor(path);
            decompressor.decompress();
        }


    }
}
