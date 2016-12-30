package io.mycat.bigmem.cacheway;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.console.ChunkMemoryAllotEnum;

/**
 * 分配的内存返回对象信息
 * @author kk
 * 2016年12月29日
 */
public class MemoryAllotRestult {

	/**
	 * 返回的buffer对象
	 */
	private MycatBufferBase mycatBuffer;

	/**
	 * 具体的内存分配器对象信息
	 */
	private ChunkMemoryAllotEnum memoryAllotEnum;

	public MemoryAllotRestult(MycatBufferBase mycatBuffer, ChunkMemoryAllotEnum memoryAllotEnum) {
		super();
		this.mycatBuffer = mycatBuffer;
		this.memoryAllotEnum = memoryAllotEnum;
	}

	public MycatBufferBase getMycatBuffer() {
		return mycatBuffer;
	}

	public void setMycatBuffer(MycatBufferBase mycatBuffer) {
		this.mycatBuffer = mycatBuffer;
	}

	public ChunkMemoryAllotEnum getMemoryAllotEnum() {
		return memoryAllotEnum;
	}

	public void setMemoryAllotEnum(ChunkMemoryAllotEnum memoryAllotEnum) {
		this.memoryAllotEnum = memoryAllotEnum;
	}

}
