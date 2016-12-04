package it.miketech;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Mike on 03/12/2016.
 */
public class Releaser {

    private static int TREE_CODE_START_INDEX = 96;
    byte[] composeFile;

    private String treeStr = ""; //字典树压缩后的字符串
    private String composedStr = ""; //前缀码字符串
    private String fileName = "";

    public Releaser() throws IOException {
        String path = "/Users/Mike/Desktop/compress.hc";
        Path mPath = Paths.get(path);
        composeFile = Files.readAllBytes(mPath);
        fileName = ByteUtil.getFileName(Arrays.copyOfRange(composeFile,0,63));
        System.out.println("读取文件名----OK");


        byte[] a = Arrays.copyOfRange(composeFile,64,96);
        int treelength = ByteUtil.getTreeLength( a );
        System.out.println("读取字典树----OK");

        byte[]  treeCodeBytes =  Arrays.copyOfRange(composeFile,TREE_CODE_START_INDEX,TREE_CODE_START_INDEX+treelength);

        String treeCode = new String(treeCodeBytes, "UTF-8");
        this.treeStr = treeCode;
        System.out.println(treeCode);

        int COMPRESSED_STR_START_INDEX = TREE_CODE_START_INDEX+treelength+1;
        int COMPRESSED_STR_END_INDEX = composeFile.length;

        byte[] composedBytes = Arrays.copyOfRange(composeFile,COMPRESSED_STR_START_INDEX,COMPRESSED_STR_END_INDEX);


        int reminder = Arrays.copyOfRange(composeFile,COMPRESSED_STR_START_INDEX-1,COMPRESSED_STR_START_INDEX)[0];
        System.out.println(getComposedStr(reminder,composedBytes));
        this.composedStr = getComposedStr(reminder,composedBytes);
        System.out.println("读取前缀表----OK");

    }

    private String getComposedStr(int reminder,byte[] bytes){

        String compsedStr = "";

        for (int i=0;i<bytes.length-1;i++){
            String temp = Integer.toBinaryString(bytes[i]);
            String temp2 = completeBinaryCode(temp);
            compsedStr += temp2;
        }


        String temp = completeBinaryCode(Integer.toBinaryString(bytes[bytes.length-1]));
        compsedStr += temp.substring(0,temp.length()-reminder);

        return compsedStr;

    }

    /**
     * 用于补全读取的二进制代码为4位
     * 10 -> 0010
     * 111 -> 0111
     * @param code
     * @return
     */
    private String completeBinaryCode(String code){
        int length = code.length();
        String temp = "";
        for(int i=0;i<4-length;i++){
            temp += '0';
        }
        return temp+code;
    }


    public void expand(){
        System.out.println("重建单词查找树----OK");
        Node root = readTire();
        ArrayList<Byte> byteList = new ArrayList<>();
        String expandStr = "";

        System.out.println("重建原文件----OK");
        while (composedStr.length() > 0){
            Node x = root;

            while ( !x.isLeaf() ){
                char a = composedStr.charAt(0);
                composedStr = composedStr.substring(1,composedStr.length());


                if ( a == '1'){
                    x = x.right;
                } else{
                    x = x.left;
                }
            }
            //System.out.println((int)x.ch - 128);
            byteList.add((byte) (x.ch - 128));
            expandStr += (char)((int)x.ch - 128);
        }

        System.out.println(expandStr);
        System.out.println("重建文件大小: "+expandStr.length());

        byte[] arr = new byte[byteList.size()];

        for (int i = 0;i< byteList.size();i++){
            arr[i] = byteList.get(i);
        }


        FileUtil.writeFile("/Users/Mike/Desktop/"+fileName,arr);

    }


    /**
     * 重建单词查找树
     * @return Root Node
     */
    private Node readTire( ){

        char a = treeStr.charAt(0);
        treeStr = treeStr.substring(1,treeStr.length());

        if (a == '1'){
            char b = treeStr.charAt(0);
            treeStr = treeStr.substring(1,treeStr.length());
            return new Node(b,0,null,null);
        }
        return new Node('\0',0,readTire(),readTire());
    }

    public static void main(String[] args) throws IOException {
        Releaser releaser = new Releaser();
        releaser.expand();
    }

}
