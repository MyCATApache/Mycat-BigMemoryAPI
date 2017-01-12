package io.mycat.bigmem.buffer;
/**
*@desc:
*@author: zhangwy   @date: 2016年12月28日 上午7:28:45
**/
public class BitSet {

	private long[] bits;
	private final int aviableNum;
	private long nextAvail;
	/**
	 * @param aviableNum
	 */
	public BitSet(int aviableNum) {
		this.aviableNum = aviableNum;
		//变成64的倍数
		int size = ((this.aviableNum - 1) >>> 6 ) + 1;
		bits = new long[size];
		nextAvail = -1;
		
	}
	
	public void set(long id) {
		checkBound(id);
		int base = (int) (id >>> 6);
		int offset = (int) (id &(0x3fL));
		long value = (1L << offset);
		System.out.println(String.format("base %d offset %d", base, offset));
		bits[base] |= value;
		print();
	}

	private void checkBound(long id) {
		//ByteBuffer
		if(id < 0 || id > aviableNum) {
			throw new IndexOutOfBoundsException(String.format("%d 不能小于 0 或者大于容量 %d", id, aviableNum)); 
		}
	}

	public void free(long id) {
		checkBound(id);
		int base = (int) (id >>> 6);
		int offset = (int) (id &(0x3fL));
		long value = ~(1L << offset);
		bits[base] &= value;
		nextAvail = id;
		System.out.println(String.format("free base %d offset %d", base, offset));
		print();
	}
	
	public long findFree() {
		for(int index = 0 ; index < bits.length; index++) {
			if((~bits[index]) != 0) {
				return findFreebit(index);				
			}
		}
		return -1;
	}
	public void print() {
		for(int i = 0 ; i < bits.length; i++) {
			System.out.println("bist " + i+ " : " + Long.toBinaryString(bits[i]));
		}
	}
	/**
	*@desc:  寻找单个long里面的空闲的位置
	*@return: long
	*@auth: zhangwy @date: 2016年12月28日 上午7:50:24
	**/
	private long findFreebit(int index) {
		if(nextAvail != -1) {
			long temp = nextAvail;
			nextAvail = -1;
			return temp;
		}
 		long value = ~bits[index];
		long flag = 0x1L ;
		for(int i = 0 ; i < 64 ; i++) {
			if((value & flag) != 0) {
				long id = (((long)index) << 6) + i;
				if(id < aviableNum) {
					return id;
				}
				return -1;
			}
			flag <<= 1;
		}
		
		return -1;
	}
//	public static void main(String[] args) {
//		MycatBitSet bitSet = new MycatBitSet(100);
//		for(int i = 64 ; i > 0; i--) {
//			bitSet.set(i);
//		}
//		bitSet.free(44);
//		bitSet.free(53);
//
//		long id = bitSet.findFree();
//		
//		System.out.println(bitSet.findFree());
//		bitSet.set(bitSet.findFree());
//		System.out.println(bitSet.findFree());
//
//	}
}

