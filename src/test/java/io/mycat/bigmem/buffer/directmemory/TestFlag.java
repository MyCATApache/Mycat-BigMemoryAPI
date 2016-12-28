package io.mycat.bigmem.buffer.directmemory;

public class TestFlag {
    
    public static void main(String[] args) {
        int tem1 = 1;
        print(tem1);
        
        int temp2 = 1<<1;
        print(temp2); 
        
        int temp3 = 1<<2;
        print(temp3); 
        
        
        int value4 = 7;
        print(value4);
        
        int value5 = 7&2;
        print(value5);
        
        int value6 = 7&4;
        print(value6);
        
        
        
    }
    
    public static void print(int value)
    {
        System.out.print(value + "\t");
        System.out.println(Integer.toBinaryString(value));
    }

}
