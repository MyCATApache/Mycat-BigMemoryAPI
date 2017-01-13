package io.mycat.bigmem.buffer;

/**
*@desc:
*@author: zhangwy   @date: 2016年12月28日 上午6:51:16
**/
public class Chunk<T> {
	private final boolean pooled; /*是否进入缓存池*/
	private final Arena<T> arena;
	private final int pageSize;
	private final int maxOrder;
	private final int pageShift;
	private final int log2ChunkSize;
	private final int chunkSize;
	private final byte unusable;
	//容器用来存放数据的地方.
	private T memory;
	private final int maskSubpage;
	
	private final int maxSubpageAllocs;
	private final Subpage<T>[] subpagesList;
	
	private final byte[] memoryMap;
	private final byte[] depth;
	
	private int freeBytes;
	//链表使用
	ChunkList<T> parent;
	Chunk<T> prev;
	Chunk<T> next;
	
	
	public Chunk(Arena<T> arena,T memory, int chunkSize, int pageSize,
			int maxOrder) {
		pooled = true;
		this.arena = arena;
		this.memory = memory;
		this.pageSize = pageSize;
		this.maxOrder = maxOrder;
		this.pageShift = log2(pageSize);
		this.log2ChunkSize = log2(chunkSize);
		this.chunkSize = chunkSize;
		this.unusable = (byte) (maxOrder + 1);
		this.maxSubpageAllocs = (1 << maxOrder);
		this.maskSubpage = ~(pageSize -1);
		subpagesList = newSubpages(this.maxSubpageAllocs);
		memoryMap = new byte[maxSubpageAllocs << 1];
		depth = new byte[maxSubpageAllocs << 1];
		int id = 1 ;
		for(int d = 0; d <= maxOrder; d++) {
			int len = 1 << d;
			for(int p = 0 ; p < len; p++) {
				memoryMap[id] = (byte) d;
				depth[id] = (byte) d;
				id++;
			}
		}
		freeBytes = chunkSize;
	}
	
	public Chunk(Arena<T> arena,T memory, int chunkSize) {
		pooled = false;
		this.arena = arena;
		this.memory = memory;
		this.pageSize = 0;
		this.maxOrder = 0;
		this.pageShift = 0;
		this.log2ChunkSize = log2(chunkSize);
		this.chunkSize = chunkSize;
		this.unusable = (byte) (maxOrder + 1);
		this.maxSubpageAllocs = (1 << maxOrder);
		this.maskSubpage = ~(pageSize -1);
		subpagesList = null;
		memoryMap = null;
		depth = null;
		freeBytes = 0;
	}
	/**
	*@desc 返回是否进入缓存池
	*@auth zhangwy @date 2017年1月5日 上午7:42:21
	**/
	public boolean getPooled() {
		return this.pooled;
	}
	/**
	 * 获取存储的容器
	 * **/
	public T getMemory() {
		return memory;
	}
	public long allocate(int normalSize) {
		if((normalSize & maskSubpage) != 0) {
			return allocateRun(normalSize);
		} else {
			return allocateSubpage(normalSize);
		}
	}
	//分配大于等于一个节点的(>=pageSize)
	private long allocateRun(int normalSize) {
		int d = maxOrder - (log2(normalSize) - pageShift) ;
		int memoryMapId = allocateNode(d);
		return memoryMapId;
	}
	
	//分配小于等于一个pageSize(<pageSize)
	private long allocateSubpage(int normalSize) {
		int memoryMapId = allocateNode(maxOrder);
		if(memoryMapId < 0) {
			return memoryMapId;
		}
		Subpage<T> subpage = subpagesList[subpageId(memoryMapId)];
		if(subpage == null) {
			subpage = new Subpage<T>(this, memoryMapId, pageSize, normalSize);
			subpagesList[subpageId(memoryMapId)] = subpage;
		} else {
			subpage.initSubpage(normalSize);
		}
		return subpage.allocate();
	}
	/** 初始化byteBuffer
	*@desc
	*@auth zhangwy @date 2017年1月2日 下午9:17:28
	**/
	public void initBuf(BaseByteBuffer<T> byteBuffer ,long handle, int capacity) {
		 int memoryMapIdx = (int) handle;
	        int bitmapIdx = (int) (handle >>> Integer.SIZE);
	        if (bitmapIdx == 0) {
	            byte val = value(memoryMapIdx);
	            assert val == unusable : String.valueOf(val);
	            byteBuffer.init(this, handle, runOffset(memoryMapIdx), capacity, runLength(memoryMapIdx));
	        } else {
	            initBufWithSubpage(byteBuffer, handle, bitmapIdx, capacity);
	        } 
	}
	/** 初始化small 或者tiny类型的bytebuffer
	*@desc
	*@auth zhangwy @date 2017年1月2日 下午9:17:28
	**/
	private void initBufWithSubpage(BaseByteBuffer<T> byteBuffer, long handle, int bitmapIdx, int capacity) {
		bitmapIdx = bitmapIdx & 0x3FFFFFFF ;
        int memoryMapIdx = (int) handle;
		Subpage<T> subpage = subpagesList[subpageId(memoryMapIdx)];
		byteBuffer.init(this, handle, runOffset(memoryMapIdx) + bitmapIdx * subpage.getElememtSize(),
				capacity, subpage.getElememtSize());
	}
	/**
	*@desc:
	*完全二叉树的跟节点最小,儿子节点大.
	*@return: int
	*@auth: zhangwy @date: 2016年12月29日 下午8:45:29
	**/
	private int allocateNode(int d) {
		int id = 1;
		byte val = value(id);
		//val需要小于等于d才说明能够被分配.
		if(d < val) {
			return -1;
		}
		//
		int targetLevel = 1 << d;
		//找到儿子中可分配的,并且到达了第d层了
		while(d < val || id < targetLevel ) {
			id = id << 1;
			val = value(id);
			//判断如果左儿子不满足分配,则使用右儿子进行分配
			if(d < val) {
				id ^= 1;
				val = value(id);
			}
		}
		
		//设置当前节点已经被使用
		setValue(id, unusable);
		//更新父亲节点的使用状态
		updateParentAlloc(id);
		freeBytes -= runLength(id);
		return id;
	}

	/**
	*@desc:
	*@return: void
	*@auth: zhangwy @date: 2016年12月29日 下午8:57:59
	**/
	private void updateParentAlloc(int id) {
		int parentId = id ;  ///除以2取得父亲节点
		while(parentId > 1) {
			byte leftValue = value(id);
			byte rightValue =  value(id ^ 1);
			parentId = id >>> 1;
			setValue(parentId, leftValue < rightValue ?leftValue : rightValue);
			id = parentId;
		}
	}
	
	private void updateParentfree(int id) {
		int parentId = id ;  ///除以2取得父亲节点
		while(parentId > 1) {
			byte leftValue = value(id);
			byte rightValue =  value(id ^ 1);
			parentId = id >>> 1;
			byte dep = depth[id];
			if(dep == leftValue && dep == rightValue) {
				setValue(parentId, (byte)(dep - 1));
			} else {
				setValue(parentId, leftValue < rightValue ?leftValue : rightValue);
			}
			id = parentId;
		}
	}
	private void freeNode(int memoryMapId) {
		//需要释放subpage 代码为实现
		setValue(memoryMapId, depth[memoryMapId]);
		freeBytes += runLength(memoryMapId);
		updateParentfree(memoryMapId);
	}
	
	/** 释放一个handle
	 * 如果是subpage的，首先释放bitMapId， 假如整个subpage都已经被释放，则释放当前的MemoryId，同时增加freeBytes
	*  如果是大于pageSize的，则直接释放当前的memoryId，同时增加freeBytes
	*@desc
	*@auth zhangwy @date 2017年1月2日 下午9:17:28
	**/
	public void free(long handle) {
		final int memoryMapIdx = (int) handle;
		int bitmapIdx = (int) (handle >>> Integer.SIZE);
		if (bitmapIdx != 0) {
			Subpage<T> subpage = subpagesList[subpageId(memoryMapIdx)];
			bitmapIdx = bitmapIdx & 0x3FFFFFFF ;
			if(subpage.free(bitmapIdx)) {
				//正在使用直接返回
				return;
			}
		}
		freeNode(memoryMapIdx);
	}
	
	private byte value(int memoryMapId) {
		return memoryMap[memoryMapId];
	}
	
	private void setValue(int memoryMapId,byte value) {
		memoryMap[memoryMapId] = value;
	} 
	
	/**@param handle 分配的句柄
	 * @return 通过分配的handle返回偏移量
	 * @author zhangwy
	 * */

	public int getOffsetByHandle(long handle) {
		int memoryMapIdx = (int) handle;
        int bitmapIdx = (int) (handle >>> Integer.SIZE);
        int offset = 0;
		if(bitmapIdx == 0) {
			offset = runOffset(memoryMapIdx);
		} else {
			bitmapIdx = bitmapIdx & 0x3FFFFFFF ;
			Subpage<T> subpage = subpagesList[subpageId(memoryMapIdx)];
			offset = runOffset(memoryMapIdx) + subpage.getElememtSize() * bitmapIdx;
		}
		return offset;
	}
	
	/*    获取当前层数的偏移量然后 * 当前层数一个节点所管理的大小*/
	private int runOffset(int memoryMapId) {
		int nodeOffset = memoryMapId ^ (1 << depth[memoryMapId]);
//		System.out.println("nodeOffset " + nodeOffset);
		return  nodeOffset * runLength(memoryMapId);
	}
	
	/*当前节点管理的大小*/
	private int runLength(int memoryMapId) {
        // represents the size in #bytes supported by node 'id' in the tree
        return 1 << log2ChunkSize -depth[memoryMapId];
    }
	
	/*最后一层memoryMapId对应的subpageId是多少,移除最高位.*/
	private int subpageId(int memoryMapId) {
		return memoryMapId ^ maxSubpageAllocs;
	}
	
	@SuppressWarnings("unchecked")
	private Subpage<T>[] newSubpages(int num) {
		return new Subpage[num];
	}

	static int log2(int value) {
		return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(value);
	}
	/**
	 * 已使用率.
	 * **/
	public int usage() {
		int freePercent = 0;
		if(freeBytes == 0) {
			return 100;
		}
		freePercent =  freeBytes * 100 / chunkSize;
		if(freePercent == 0) {
			return 99;
		}
		return 100 - freePercent;
	}
	public Arena<T> getArena() {
		return this.arena;
	}
	
	
	 @Override
	public String toString() {
	    return new StringBuilder()
	    .append("Chunk(")
	    .append(Integer.toHexString(System.identityHashCode(this)))
	    .append(": ")
	    .append(usage())
	    .append("%, ")
	    .append(chunkSize - freeBytes)
	    .append('/')
	    .append(chunkSize)
	    .append(')')
	     .toString();
	}
	
	public static void main(String[] args) {
		System.out.println(log2(8192));
		int pageSize = 1024;
		int maxOrder = 3;
		int chunkSize = (1 << maxOrder) * pageSize;
		System.out.println(chunkSize);

		//Chunk<DirectBuffer> chunk = new Chunk<DirectBuffer>(
		//		new Arena<DirectBuffer>(), (DirectBuffer)ByteBuffer.allocateDirect(chunkSize) ,chunkSize, pageSize,maxOrder);
		//System.out.println(chunk.allocateRun(1024));
		//System.out.println("======" + chunk.usage());

	}
	
}

