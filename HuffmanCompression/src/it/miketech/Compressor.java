package it.miketech;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Created by Mike on 01/12/2016.
 */
public class Compressor {

    private String treeStr = ""; //字典树压缩后的字符串
    private String composedStr = ""; //前缀码字符串


    private static final int R = 256; //ASCII码表


    private String[] buildCode(Node root){
        String[] st = new String[R]; //字符索引
        buildCode(st,root,"");
        return st;
    }

    /**
     * 根据单词查找树构建编译表
     * @param st
     * @param x
     * @param str
     */
    private void buildCode(String[] st,Node x,String str){

        if (x == null){
          return;
        }

        if (x.isLeaf()){
            st[ x.ch ] = str;
        }

        buildCode(st, x.left, str + '0');
        buildCode(st, x.right, str + '1' );

    }

    /**
     * 构建单词查找树
     * @param freq
     * @return
     */
    private Node buildTrie(int[] freq){
        //优先队列
        PriorityQueue<Node> pq = new PriorityQueue<Node>();

        for (char c = 0; c<R;c++){//ASCII码表
            if (freq[c]>0)
                pq.add(new Node(c,freq[c],null,null));
        }

        //合并
        while (pq.size() > 1){
            Node left = pq.poll();//弹出最小元素
            Node right = pq.poll();
            Node parent = new Node('\0',left.freq+right.freq,left,right);
            pq.add(parent);
        }

        return pq.poll();

    }

    /**
     * 将单词查找树转换为比特
     * @param x
     */
    private void writeTire(Node x){
        if (x.isLeaf()){
            //System.out.print(1);
           // System.out.print(x.ch);
            treeStr += '1';
            treeStr += x.ch;
            return;
        }
       // System.out.print(0);
        treeStr += '0';
        writeTire(x.left);
        writeTire(x.right);
    }

//
//    /**
//     * 重建单词查找树
//     * @return Root Node
//     */
//    private Node readTire( ){
//
//        char a = treeStr.charAt(0);
//        treeStr = treeStr.substring(1,treeStr.length());
//
//        if (a == '1'){
//            char b = treeStr.charAt(0);
//            treeStr = treeStr.substring(1,treeStr.length());
//            return new Node(b,0,null,null);
//        }
//        return new Node('\0',0,readTire(),readTire());
//    }

    public void compress() throws IOException {

        String path = "/Users/Mike/Desktop/1.txt";

        System.out.println("开始压缩: "+path);

        int[] freq = new int[R];

        Path mPath = Paths.get(path);
        byte[] data = Files.readAllBytes(mPath);
        //FileUtil.writeFile("/Users/Mike/Desktop/233.png",data);

        for (byte b:data){
            freq[b+128]++;
        }

        System.out.println("生成频率表----OK");

        Node root = buildTrie(freq);

        //编译表
        String[] st = new String[R];
        st = buildCode(root);
        System.out.println("生成编译表----OK");
       // printCodeTable(st);


        //压缩
        for (int i=0;i<data.length;i++){
            int index = data[i] + 128;
            composedStr+= st[index];
        }

        System.out.println("生成前缀表----OK");

        //生成字典树字符串
        writeTire(root);
        System.out.println(treeStr);
        System.out.println(composedStr);
        System.out.println("生成字典树----OK");
        byte[] fileName = ByteUtil.getFileNameByteArr("test.txt");
        byte[] treeBytes = treeStr.getBytes();
        byte[] treeLength = ByteUtil.getByteArrForTreeLength(treeBytes.length);
        byte[] reminder = {ByteUtil.getReminderByte(composedStr)};
        byte[] compBinBytes = ByteUtil.binStrToByteArr(composedStr);



        byte[] a = combine(fileName,treeLength);
        byte[] b = combine(a,treeBytes);
        byte[] c = combine(b,reminder);
        byte[] d = combine(c,compBinBytes);

        System.out.println("压缩后长度(比特): "+d.length);

        FileUtil.writeFile("/Users/Mike/Desktop/compress.hc",d);

    }

    public static byte[] combine(byte[] a, byte[] b){
        int length = a.length + b.length;
        byte[] result = new byte[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }



    private void printCodeTable(String[] st){
        for (int i=0;i<R;i++){
            if (st[i]!=null){
                System.out.println((char)i+": "+st[i]);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Compressor c = new Compressor();
        c.compress();
       // c.expand();
    }

}
