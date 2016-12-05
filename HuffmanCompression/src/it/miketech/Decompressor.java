package it.miketech;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Mike on 03/12/2016.
 */
public class Decompressor {

    private static int TREE_CODE_START_INDEX = 96;
    private byte[] compressedFile;

    private String treeStr = ""; //字典树压缩后的字符串
    private String compressedStr = ""; //前缀码字符串
    private String fileName = "";

    private String absPath = "";//路径


    public Decompressor(String filePath) throws IOException {

        File tempFile =new File(filePath);
        absPath = tempFile.getParentFile().getCanonicalPath()+"/";

        //读取压缩文件
        compressedFile = FileUtil.readFileByBytes(filePath);

        //获得原始文件名 0-64字节
        fileName = ByteUtil.getFileName(Arrays.copyOfRange(compressedFile,0,63));

        //读取单词查找树长度64-96字节
        byte[] a = Arrays.copyOfRange(compressedFile,64,96);
        int treeLength = ByteUtil.getTreeLength( a );

        //读取单词查找树
        byte[]  treeCodeBytes =  Arrays.copyOfRange(compressedFile,TREE_CODE_START_INDEX,TREE_CODE_START_INDEX+treeLength);
        String treeCode = new String(treeCodeBytes, "UTF-8");
        this.treeStr = treeCode;
        System.out.println("读取单词查找树");

        //读取前缀表
        int COMPRESSED_STR_START_INDEX = TREE_CODE_START_INDEX+treeLength+1;
        int COMPRESSED_STR_END_INDEX = compressedFile.length;
        byte[] composedBytes = Arrays.copyOfRange(compressedFile,COMPRESSED_STR_START_INDEX,COMPRESSED_STR_END_INDEX);
        int reminder = Arrays.copyOfRange(compressedFile,COMPRESSED_STR_START_INDEX-1,COMPRESSED_STR_START_INDEX)[0];
        this.compressedStr = getCompressedStr(reminder,composedBytes);
        System.out.println("读取前缀表");

    }

    /**
     * 移除之前的不足四位的补全
     * @param reminder
     * @param bytes
     * @return
     */
    private String getCompressedStr(int reminder, byte[] bytes){

        String compressedStr = "";

        for (int i=0;i<bytes.length-1;i++){
            String temp = Integer.toBinaryString(bytes[i]);
            String temp2 = completeBinaryCode(temp);
            compressedStr += temp2;
        }


        String temp = completeBinaryCode(Integer.toBinaryString(bytes[bytes.length-1]));
        compressedStr += temp.substring(0,temp.length()-reminder);

        return compressedStr;

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


    public void decompress(){

        System.out.println("开始解压缩"+fileName);

        Node root = readTire();
        System.out.println("重建单词查找树");
        ArrayList<Byte> byteList = new ArrayList<>();


        System.out.println("重建原文件");
        while (compressedStr.length() > 0){
            Node x = root;

            while ( !x.isLeaf() ){
                char a = compressedStr.charAt(0);
                compressedStr = compressedStr.substring(1, compressedStr.length());


                if ( a == '1'){
                    x = x.right;
                } else{
                    x = x.left;
                }
            }
            //System.out.println((int)x.ch - 128);
            byteList.add((byte) (x.ch - 128));

        }

        System.out.println("重建文件大小: "+byteList.size()+" 比特");

        byte[] arr = new byte[byteList.size()];

        for (int i = 0;i< byteList.size();i++){
            arr[i] = byteList.get(i);
        }


        String filePrefix = "de_";
        FileUtil.writeFile(absPath+filePrefix+fileName,arr);
        System.out.println("解压缩完成");
    }



    public static void main(String[] args) throws IOException {
        String path = "/Users/Mike/Desktop/compress.hc";
        Decompressor decompressor = new Decompressor(path);
        decompressor.decompress();
    }

}
