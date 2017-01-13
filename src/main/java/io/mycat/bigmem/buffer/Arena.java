package io.mycat.bigmem.buffer;

import io.mycat.bigmem.util.MathUtil;
import io.mycat.bigmem.util.StringUtil;

/**
** @desc:
** @author: zhangwy   
* @date: 2016年12月28日 上午6:48:19
**/
public abstract class Arena<T> {
	/**
	 * 
	 * 
	 */
	private final int chunkSizeNum = 4;
	//hugeChunkSize = ChunkSize * chunkSizeNum
	private final int hugeChunkSize  ; //
	private final int chunkSizeMask  ; //
	private final int chunkShifts;
	private final int tinySubpagePoolSize = 512 >>> 4;
	private final int subpageOverflowMask ;
	protected final int pageSize;
	protected final int pageShift ; 
	protected final int maxOrder;
	protected final int chunkSize;
	protected final int smallSubpagePoolSize; 
	private final Subpage<T>[] tinySubpagePool ;
	private final Subpage<T>[] smallSubpagePool;
	
    private final ChunkList<T> q050;
    private final ChunkList<T> q025;
    private final ChunkList<T> q000;
    private final ChunkList<T> qInit;
    private final ChunkList<T> q075;
    private final ChunkList<T> q100;
    
    /**
     * @param pageSize 页大小
     * @param chunkSize chunk的大小
     * @param maxOrder 完全二叉树多少次层
     * **/
	protected Arena(int pageSize,int chunkSize,int maxOrder) {
		this.chunkSize = chunkSize;
		this.chunkSizeMask = this.chunkSize - 1;
		this.chunkShifts = MathUtil.log2p(this.chunkSize);
		this.pageSize = pageSize;
		this.maxOrder = maxOrder;
		this.pageShift = MathUtil.log2p(pageSize);
		this.subpageOverflowMask = ~(pageSize - 1); /*用来判断是否小于一个pageSize的*/
		this.hugeChunkSize = chunkSize << chunkSizeNum;
		tinySubpagePool = newSupagePoolHeader(tinySubpagePoolSize, pageSize , 4);
		smallSubpagePoolSize = this.pageShift - 9;
		smallSubpagePool = newSupagePoolHeader(smallSubpagePoolSize, pageSize, 9);
		
        q100 = new ChunkList<T>(this, null, 100, Integer.MAX_VALUE);
        q075 = new ChunkList<T>(this, q100, 75, 100);
        q050 = new ChunkList<T>(this, q075, 50, 100);
        q025 = new ChunkList<T>(this, q050, 25, 75);
        q000 = new ChunkList<T>(this, q025, 1, 50);
        qInit = new ChunkList<T>(this, q000, Integer.MIN_VALUE, 25);
        
        q100.setPre(q075);
        q075.setPre(q050);
        q050.setPre(q025);
        q025.setPre(q000);
        q000.setPre(null);
        qInit.setPre(qInit);
        
	}
	/*分配byteBuffer用的*/
	public BaseByteBuffer<T> allocateBuffer(int capacity) {
		BaseByteBuffer<T> buffer = newBuffer(capacity);
		allocate(buffer, capacity);
		return buffer;
	}
	
	
	/**
	*@desc
	*@auth zhangwy @date 2017年1月2日 下午8:38:20
	**/
	private void allocate(BaseByteBuffer<T> buffer, int capacity) {
		int normalSize = normalizeCapacity(capacity);
		if(isTinyOrSmall(normalSize)) {
			Subpage<T>[] table;
			int tableId;
			if(isTiny(normalSize)) {
				table = tinySubpagePool;
				tableId = tinyId(normalSize);
			} else {
				table = smallSubpagePool;
				tableId = smallId(normalSize);
			}
			synchronized (this) {
				final Subpage<T> head = table[tableId];
				final Subpage<T> s = head.next;
				if(s != head) {
					long handle = s.allocate();
					s.getChunk().initBuf(buffer, handle, capacity);
					return ;
				}
			}
			allocateNormal(buffer, capacity, normalSize);
			return ;
		} else if(normalSize <= chunkSize){
			//分配大于pageSize 小于chunkSize
			allocateNormal(buffer, capacity, normalSize);
			return ;
		} else if(capacity <= hugeChunkSize ) {
			allocateHuge(buffer, capacity);
		} else	{
			//分配hugeOrSuper
			allocateSuper(buffer, capacity);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void allocateHuge(BaseByteBuffer<T> buffer, int capacity) {
		int nChunk = capacity >>> chunkShifts;
		int lastChunkSize = capacity & chunkSizeMask;
		Chunk<T>[] chunkArray = null;
		if(lastChunkSize != 0) {
			chunkArray = newUpooledChunks(nChunk + 1, true);
			Handle handle = allocateHandle(lastChunkSize);
			//把最后一个chunk加入到chunk数组中
			final Chunk<T> c = handle.getChunk();
			chunkArray[nChunk] = c;
			int lastChunkNormalSize =  normalizeCapacity(lastChunkSize);
			buffer.init(chunkArray, handle.getHandle(), c.getOffsetByHandle(
					handle.getHandle()), lastChunkSize,lastChunkNormalSize);
		} else {
			chunkArray = newUpooledChunks(nChunk , false);
			buffer.init(chunkArray, 0L, 0, chunkSize ,chunkSize);
		}
	}
	/**
	*@desc 创建一个hugeChunk 不进入缓存池,并却初始化baseByteBuffer
	*@auth zhangwy @date 2017年1月2日 下午8:59:37
	**/
	private void allocateSuper(BaseByteBuffer<T> buffer, int capacity) {
		Chunk<T> hugeChunk = newUnpoolChunk(capacity);
		buffer.initUnpooled(hugeChunk, capacity);
	}
	
	/**
	*@desc 分配一个tiny ,small, normal大小的handle
	*@auth zhangwy @date 2017年1月7日 上午8:13:26
	**/
	private Handle allocateHandle(int capacity) {
		int normalSize = normalizeCapacity(capacity);
		if(isTinyOrSmall(normalSize)) {
			Subpage<T>[] table;
			int tableId;
			if(isTiny(normalSize)) {
				table = tinySubpagePool;
				tableId = tinyId(normalSize);
			} else {
				table = smallSubpagePool;
				tableId = smallId(normalSize);
			}
			synchronized (this) {
				final Subpage<T> head = table[tableId];
				final Subpage<T> s = head.next;
				if(s != head) {
					long handle = s.allocate();
					return new Handle(handle, s.getChunk(), normalSize);
				}
			}
		} 
		return allocateNormalHandle(capacity, normalSize);
	}
	/**
	*@desc 
	*@auth zhangwy @date 2017年1月7日 上午8:31:06
	**/
	private synchronized Handle allocateNormalHandle(int capacity, int normalSize) {
		Handle handleObj = null;
		if ( (handleObj = q050.allocateHandle(capacity, normalSize)) != null || (handleObj = q025.allocateHandle( capacity, normalSize)) != null
			|| (handleObj = q000.allocateHandle(capacity, normalSize)) != null || (handleObj = qInit.allocateHandle( capacity, normalSize)) != null
			|| (handleObj = q075.allocateHandle(capacity, normalSize)) != null || (handleObj = q100.allocateHandle( capacity, normalSize)) != null) {
	            return handleObj;
	     }
        // Add a new chunk.
       	Chunk<T> c = newChunk();
        long handle = c.allocate(normalSize);
        assert handle > 0;
        qInit.addChunk(c);
		return new Handle(handle, c, normalSize);
	}
	/**
	*@desc 创建若干不缓存的chunk,然后通过lastElementEmpty判断最后一个是否要放空.
	*@auth zhangwy @date 2017年1月7日 上午7:54:35
	**/
	@SuppressWarnings("unchecked")
	private Chunk<T>[] newUpooledChunks(int count,boolean lastElementEmpty) {
		Chunk<T>[] chunkArray = new Chunk[count];
		int size = count;
		if(lastElementEmpty) {
			size = size - 1;
		}
		for(int i = 0 ; i < size ; i++) {
			chunkArray[i] = newUnpoolChunk(chunkSize);
		}
		return chunkArray;
	}
	
	/**
	*@desc 
	*@auth zhangwy @date 2017年1月2日 下午8:58:50
	**/
	private synchronized void allocateNormal(BaseByteBuffer<T> buffer, int capacity, int normalSize) {
		if (q050.allocate(buffer, capacity, normalSize) || q025.allocate(buffer, capacity, normalSize) ||
	            q000.allocate(buffer, capacity, normalSize) || qInit.allocate(buffer, capacity, normalSize) ||
	            q075.allocate(buffer, capacity, normalSize) || q100.allocate(buffer, capacity, normalSize)) {
	            return;
	        }

        // Add a new chunk.
       	Chunk<T> c = newChunk();
        long handle = c.allocate(normalSize);
        assert handle > 0;
        c.initBuf(buffer, handle, normalSize);
        qInit.addChunk(c);
	}
	/**
	 * 对于tinysize 变为16的倍数
	 * 对于smallsize变为2的次方**/
	int normalizeCapacity(int reqCapacity) {
		if (reqCapacity < 0) {
		    throw new IllegalArgumentException("capacity: " + reqCapacity + " (expected: 0+)");
		}
		if (reqCapacity >= chunkSize) {
		    return reqCapacity;
		}
		if (!isTiny(reqCapacity)) { // >= 512
		    // Doubled
		    int normalizedCapacity = reqCapacity;
		    normalizedCapacity --;
		    normalizedCapacity |= normalizedCapacity >>>  1;
		    normalizedCapacity |= normalizedCapacity >>>  2;
		    normalizedCapacity |= normalizedCapacity >>>  4;
		    normalizedCapacity |= normalizedCapacity >>>  8;
		    normalizedCapacity |= normalizedCapacity >>> 16;
		    normalizedCapacity ++;
		
		    if (normalizedCapacity < 0) {
		        normalizedCapacity >>>= 1;
		    }
		    return normalizedCapacity;
		}
		// Quantum-spaced
		if ((reqCapacity & 15) == 0) {
		    return reqCapacity;
		}
		return (reqCapacity & ~15) + 16;
	}
	/**
	*@desc
	*@auth zhangwy @date 2016年12月31日 上午9:52:21
	**/
	private Subpage<T>[] newSupagePoolHeader(int size,int pageSize, int scale) {
		@SuppressWarnings("unchecked")
		Subpage<T>[] list = new Subpage[size];
		for(int i = 0 ; i < size; i++) {
			list[i] = new Subpage<T>(i << scale,pageSize);
		}
		return list;
	}
	/** < pageSize
	**/
	boolean isTinyOrSmall(int normalSize) {
		return (normalSize & subpageOverflowMask) == 0;
	}
	/**
	* < 512
	**/
	boolean isTiny(int normalSize) {
		return (normalSize & 0xfffffE00) == 0;
	}
	/**
	*@desc 返回tiny类型的tableId
	*@auth zhangwy @date 2016年12月31日 上午8:09:30
	**/
	private int tinyId(int normalSize) {
		return normalSize >>> 4;
	}
	/**
	*@desc
	*@auth zhangwy @date 2016年12月31日 上午8:11:17
	**/
	private int smallId(int normalSize) {
		//Integer.SIZE - 1 - Integer.numberOfLeadingZeros(normCapacity) - 9
		//=log2(normCapacity) - 9
		return Integer.SIZE - Integer.numberOfLeadingZeros(normalSize) - 10;	
	}
	/**
	*@desc:
	*@return: void
	*@auth: zhangwy @date: 2016年12月31日 上午8:06:57
	**/
	public Subpage<T> findSubpagePoolHead(int normalSize) {
		Subpage<T>[] table;
		int tableId;
		if(isTiny(normalSize)) {
			table = tinySubpagePool;
			tableId = tinyId(normalSize);
		} else {
			table = smallSubpagePool;
			tableId = smallId(normalSize);
		}
		return table[tableId];
	}
	/**创建一个新的chunk
	*@desc
	*@auth zhangwy @date 2017年1月2日 下午6:21:04
	**/
	public abstract Chunk<T> newChunk();
	/**
	*@desc
	*@auth zhangwy @date 2017年1月5日 上午7:34:56
	**/
	public abstract Chunk<T> newUnpoolChunk(int capacity) ;
	/*
	 * 创建一个新的bytegBuffer
	 * **/
	public abstract BaseByteBuffer<T> newBuffer(int capacity);
	
	/*
	 * 释放byteBuffer*/
	public abstract void freeChunk(Chunk<T> chunk);	
	
	@Override
    public synchronized String toString() {
        StringBuilder buf = new StringBuilder()
            .append("Chunk(s) at 0~25%:")
            .append(StringUtil.NEWLINE)
            .append(qInit)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 0~50%:")
            .append(StringUtil.NEWLINE)
            .append(q000)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 25~75%:")
            .append(StringUtil.NEWLINE)
            .append(q025)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 50~100%:")
            .append(StringUtil.NEWLINE)
            .append(q050)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 75~100%:")
            .append(StringUtil.NEWLINE)
            .append(q075)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 100%:")
            .append(StringUtil.NEWLINE)
            .append(q100)
            .append(StringUtil.NEWLINE)
            .append("tiny subpages:");
        	appendPoolSubPages(buf, tinySubpagePool);
        buf.append(StringUtil.NEWLINE)
           .append("small subpages:");
        appendPoolSubPages(buf, smallSubpagePool);
        buf.append(StringUtil.NEWLINE);

        return buf.toString();
    }
	/**
	*@desc
	*@auth zhangwy @date 2017年1月4日 上午7:51:48
	**/
	private void appendPoolSubPages(StringBuilder buf, Subpage<T>[] pool) {
		for(int i = 0; i < pool.length; i++) {
			Subpage<T> cur = pool[i];
			buf.append("header elememtSize:" + cur.getElememtSize() );
			while(cur.next != pool[i]) {
				cur = cur.next;
				buf.append(cur).append("   ");
			}
			buf.append(StringUtil.NEWLINE);
		}
	}
	/**
	*@desc 释放一个handle的byteBuffer
	*@auth zhangwy @date 2017年1月4日 下午11:48:33
	**/
	public void free(Chunk<T> chunk, long handle,int normalSize) {
		/*暂时先全部锁定...后面需要修改.*/
		synchronized (this) {
			if(!chunk.getPooled()) {
				freeChunk(chunk);
			} else {
				if(!chunk.parent.free(chunk, handle)){
					freeChunk(chunk);
				}
			}
		}
	}
	public int getTinySubpagePoolSize() {
		return tinySubpagePoolSize;
	}
	public int getSmallSubpagePoolSize() {
		return smallSubpagePoolSize;
	}
	
}

