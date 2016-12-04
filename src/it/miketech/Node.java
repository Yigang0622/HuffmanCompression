package it.miketech;

/**
 * Created by Mike on 01/12/2016.
 */
public class Node implements Comparable<Node>{


    public char ch; //为子节点时有用,储存当前字符

    public int freq;  //ch出现的频数

    public final Node left,right; //左右节点

    public Node(char ch, int freq, Node left, Node right) {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    //判断子叶
    public boolean isLeaf(){
        return left == null && right == null;
    }

    //比较器
    @Override
    public int compareTo(Node that) {
        return this.freq - that.freq;
    }
}
