
package io.mycat.bigmem.buffer;

/**
*@desc 分配的内存的句柄
*@author zhangwy   @date 2017年1月7日 上午8:25:53
**/
public class Handle {
	
	private long handle;
	private Chunk chunk;
	private int capacity;
	
	/**
	 * @param handle
	 * @param chunk
	 * @param capacity
	 */
	public Handle(long handle, Chunk chunk, int capacity) {
		super();
		this.handle = handle;
		this.chunk = chunk;
		this.capacity = capacity;
	}
	public long getHandle() {
		return handle;
	}
	public void setHandle(long handle) {
		this.handle = handle;
	}
	public Chunk getChunk() {
		return chunk;
	}
	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}

