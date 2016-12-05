package it.miketech;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * Created by Mike on 01/12/2016.
 */
public class Compressor {

    private String treeStr = ""; //字典树压缩后的字符串
    private String compressedStr = ""; //前缀码字符串

    private String originalFileName = ""; //文件名
    private String absPath = "";//路径
    private String filePath = ""; //路径+文件名

    private static final int R = 256; //ASCII码表

    Compressor(String filePath) throws IOException {
        this.filePath = filePath;
        File tempFile =new File(filePath);
        originalFileName = tempFile.getName();
        absPath = tempFile.getParentFile().getCanonicalPath()+"/";
    }


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
            treeStr += '1';
            treeStr += x.ch;
            return;
        }

        treeStr += '0';
        writeTire(x.left);
        writeTire(x.right);
    }


    public void compress() throws IOException {


        System.out.println("开始压缩: "+filePath);

        int[] freq = new int[R]; //频率表

        byte[] data = FileUtil.readFileByBytes(filePath);

        for (byte b:data){
            freq[b+128]++;
        }

        System.out.println("生成频率表");

        Node root = buildTrie(freq);

        //编译表
        String[] st = new String[R];
        st = buildCode(root);
        System.out.println("生成编译表");

        //压缩
        for (int i=0;i<data.length;i++){
            int index = data[i] + 128;
            compressedStr += st[index];
        }
        System.out.println("生成前缀表");


        writeTire(root);
        System.out.println("生成单词查找树");


        byte[] fileName = ByteUtil.getFileNameByteArr(originalFileName);
        byte[] treeBytes = treeStr.getBytes();
        byte[] treeLength = ByteUtil.getByteArrForTreeLength(treeBytes.length);
        byte[] reminder = {ByteUtil.getReminderByte(compressedStr)};
        byte[] compBinBytes = ByteUtil.binStrToByteArr(compressedStr);



        byte[] a = combine(fileName,treeLength);
        byte[] b = combine(a,treeBytes);
        byte[] c = combine(b,reminder);
        byte[] d = combine(c,compBinBytes);

        System.out.println("压缩后大小: "+d.length+"比特");

        FileUtil.writeFile(absPath+"compress.hc",d);
        System.out.println("压缩文件: "+absPath+"compress.hc 生成完毕");
    }

    //合并 byte数组
    private static byte[] combine(byte[] a, byte[] b){
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
        String path = "/Users/Mike/Desktop/x.txt";
        Compressor c = new Compressor(path);
        c.compress();
       // c.expand();
    }

}
