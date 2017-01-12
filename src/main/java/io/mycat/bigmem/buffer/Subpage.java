package io.mycat.bigmem.buffer;
/**
*@desc:
*@author: zhangwy   @date: 2016年12月28日 上午6:51:00
**/
public class Subpage<T> {
	private Chunk<T> chunk; /*属于哪个chunk*/
	private int memoryMapIdx; /*chunk中的memoryIdx*/
	private long runOffset; /*地址偏移*/
	private long pageSize; /*pageSize*/
	private int elememtSize; /*分配的元素多大*/
	
	Subpage<T> next; /*前指针*/
	Subpage<T> prev;/*后指针*/
	
	private BitSet bitMap; /*使用位图*/
	private int aviableNum; /*可分配的个数*/
	private int maxNum; /*最多可分配的个数*/
	
	/*作为头指针的初始化*/
	public Subpage(int size, int pageSize) {
		this.memoryMapIdx = -1;
		this.chunk = null;
		this.elememtSize = size;
		this.pageSize = pageSize;
		this.bitMap = null;
		this.prev = this;
		this.next = this;
	}
	/*作为chunk的初始化*/
	public Subpage(Chunk<T> chunk,int memoryMapIdx, long pageSize, int size) {
		this.memoryMapIdx = memoryMapIdx;
		this.chunk = chunk;
		this.pageSize = pageSize;
		initSubpage(size);
	}
	
	/** 初始化一个subpage页面
	*@desc:
	*@return: void
	*@auth: zhangwy @date: 2016年12月28日 上午7:24:44
	**/
	public void initSubpage(int size) {
		this.elememtSize = size;
		if(elememtSize != 0) {
			maxNum = aviableNum =(int)(pageSize / elememtSize);
			bitMap = new BitSet(aviableNum);
		}
		addPool();
	}
		
	private void addPool() {
		Subpage<T> header = this.chunk.getArena().findSubpagePoolHead(elememtSize);
		this.next = header.next;
		if(header.next != null) {
			header.next.prev = this;
		}
		header.next = this;
		this.prev = header;
	}
	
	private void removePool() {
		Subpage<T> header = this.chunk.getArena().findSubpagePoolHead(elememtSize);
		prev.next = next;
		if(next != null){
			next.prev = prev;
		}
		next = null;
		prev = null;
				
	}
	/*分配一个elementSize的大小*/
	public long allocate() {
		if(elememtSize == 0 ) toHandle(0);
		if(aviableNum <= 0) return -1;
		long bitMapId = bitMap.findFree();
		long handle = toHandle(bitMapId);
		bitMap.set(bitMapId);
		aviableNum -- ;
		/*完全分配完了,从链表中移除*/
		if(aviableNum == 0) {
			removePool();
		}
		return handle;
	}
	/*返回当前的chunk是否正在使用,
	 * true: 正在使用,
	 * flase: 不在使用了,可以回收了*/
	public boolean free(long bitMapId) {
		if(elememtSize == 0) return true;
		bitMap.free(bitMapId);
		/*有空闲的,加入到链表,继续分配*/
		if(aviableNum ++ == 0) {
			addPool();
		}
		
		if(aviableNum != maxNum) {
			return true;
		} else {
			 // Subpage not in use (numAvail == maxNumElems)
            if (prev == next) {
                // Do not remove if this subpage is the only one left in the pool.
                return true;
            }
            //将subpage整块移除
			removePool();
			return false;
		}
		
	}
	/**/
    private long toHandle(long bitmapIdx) {
        return 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
    }
    /**
	 * @return the chunk
	 */
	public Chunk getChunk() {
		return chunk;
	}
	/**
	 * @return the elememtSize
	 */
	public int getElememtSize() {
		return elememtSize;
	}
	
	@Override
    public String toString() {
        return String.valueOf('(') + memoryMapIdx + ": " + (maxNum - aviableNum) + '/' + maxNum +
               ", offset: " + runOffset + ", length: " + pageSize + ", elememtSize: " + elememtSize + ')';
    }
	
    public static void main(String[] args) {

    	System.out.println(Integer.toBinaryString(0xffffffff));
    	System.out.println(Integer.toBinaryString(0xffffffff >>>1));
    	System.out.println(0xffffffff);
    	System.out.println(-1>>>1);
    	System.out.println(-1 >>1);

//    	 int normalizedCapacity = 0x7fffffff;
//         normalizedCapacity --;
//         normalizedCapacity |= normalizedCapacity >>>  1;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         normalizedCapacity |= normalizedCapacity >>>  2;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         normalizedCapacity |= normalizedCapacity >>>  4;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         normalizedCapacity |= normalizedCapacity >>>  8;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         normalizedCapacity |= normalizedCapacity >>> 16;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         normalizedCapacity ++;
//         System.out.println(Integer.toBinaryString(normalizedCapacity));
//         if (normalizedCapacity < 0) {
//             normalizedCapacity >>>= 1;
//         }
//         System.out.println(normalizedCapacity);
	}
}

