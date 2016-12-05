package it.miketech;

import java.io.UnsupportedEncodingException;

/**
 * 字节操作类
 * Created by Mike on 02/12/2016.
 */
public class ByteUtil {

    /**
     * 压缩前缀码时候使用,转换为字节数组用于存入文件
     * 如果 0101用string转byte的话一位 0 或者 1就要占用 1 byte
     * 这样转换可以把4个01存为1个byte
     * @param binStr
     * @return
     */
    public static byte[] binStrToByteArr(String binStr){

        int segment = binStr.length()/4; //段
        int reminder = binStr.length()%4==0? 0 : 4 - binStr.length()%4;    //余数

        if (reminder!=0){
            segment++;
        }

        for (int i=0;i<reminder;i++){
            binStr = binStr+'0';
        }


        byte[] bytes = new byte[segment];
        for (int i=0;i< segment;i++){
            String temp = binStr.substring(i*4,i*4+4);
            bytes[i] = Byte.parseByte(temp, 2);
        }

        return bytes;

    }

    /**
     * 剩余补齐,如果前缀码的长度不是8的整数倍
     * @param binStr
     * @return
     */

    public static byte getReminderByte(String binStr){
        int reminder = binStr.length()%4==0? 0 : 4 - binStr.length()%4;
        return (byte) reminder;
    }

    /**
     * 用于解压缩,生成文件头部32字节用来表示字典查找树的长度
     * @param treeLen
     * @return
     */
    public static byte[] getByteArrForTreeLength(int treeLen){
        int len = String.valueOf(treeLen).length();

        byte[] arr = new byte[32];

        for (int i=0;i<32-len;i++){
                arr[i] = 0;
        }

        for (int i=31;i>=32-len;i--){
            arr[i] = (byte) (treeLen%10);
            treeLen /= 10;
        }


        return arr;
    }

    /**
     * 根据文件23-96字节得到树的长度
     * @param bytes
     * @return
     */
    public static int getTreeLength(byte[] bytes){


        int startIndex = 0;
        while (bytes[startIndex] == 0){
            startIndex ++;
        }

        int sum = 0;
        for (int i=startIndex;i<32;i++){
            int muliplier = (int) Math.pow(10,32-i-1);
            sum += bytes[i]*muliplier;
        }

        return sum;
    }


    public static byte[] getFileNameByteArr(String fileName){


        byte[] temp = fileName.getBytes();


        byte[] bytes = new byte[64];
        for (int i=0;i<64;i++){
            if (i < temp.length){
                bytes[i] = temp[i];
            }else{
                bytes[i] = 32;
            }
        }


        return bytes;
    }

    public static String getFileName(byte[] bytes){
        String name = "noname";
        try {
             name = new String(bytes, "UTF-8").trim();
            return name;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       return name;
    }


    public static void main(String[] args){
       String bin = "1101110010";
//        System.out.println(getReminderByte(bin));
      byte[] bytes = binStrToByteArr(bin);
//        System.out.println(getTreeLength(bytes));
      //  byte[] bytes = getFileNameByteArr("Creative Clould Files.png");
      //  System.out.println(getFileName(bytes));
    }

}
