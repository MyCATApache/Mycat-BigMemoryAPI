package io.mycat.bigmem.util;

public class MathUtil {
	/*
	 * 求log
	 * */
    public static int log2p(int x) {
        int r = 0;
        while ((x >>= 1) != 0)
            r++;
        return r;
    }
    /*
     * @desc 返回最接近x的2的n次方的
     *
     * */
    public static int roundToPowerOfTwo(int x) {
    	x --;
    	x |= x >>>  1;
	    x |= x >>>  2;
	    x |= x >>>  4;
	    x |= x >>>  8;
	    x |= x >>> 16;
	    x++;
	    if (x < 0) {
	        x >>>= 1;
	    }
    	return x;
    }
    public static void main(String[] args) {
		System.out.println(roundToPowerOfTwo(699999999));
	}
}
