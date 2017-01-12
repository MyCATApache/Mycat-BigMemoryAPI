
package io.mycat.bigmem.buffer;

import java.nio.ByteBuffer;

import sun.nio.ch.DirectBuffer;

/**
*@desc
*@author zhangwy   @date 2017年1月2日 下午6:08:54
**/
public class DirectArena extends Arena<ByteBuffer>{

	/**
	 * @param pageSize
	 * @param chunkSize
	 * @param maxOrder
	 */
	DirectArena(int pageSize, int chunkSize, int maxOrder) {
		super(pageSize, chunkSize, maxOrder);
	}

	public Chunk<ByteBuffer> newChunk() {
		Chunk<ByteBuffer> chunk = new Chunk<ByteBuffer>(this, ByteBuffer.allocateDirect(chunkSize), 
				chunkSize, pageSize, maxOrder);
		return chunk;
	}
	public BaseByteBuffer<ByteBuffer> newBuffer(int capacity) {
		return DirectByteBuffer.newInstance(capacity);
	}
	
	public void freeChunk(Chunk<ByteBuffer> chunk) {
		//todo:待实现
//		chunk.getMemory().cleaner().clean();
		chunk.getMemory().clear();
	}


	/*创建一个不缓存的chunk 
	 * @see io.mycat.bigmem.buffer.Arena#newUnpoolChunk(int)
	 */
	@Override
	public Chunk<ByteBuffer> newUnpoolChunk(int capacity) {
		Chunk<ByteBuffer> chunk = new Chunk<ByteBuffer>(this, ByteBuffer.allocateDirect(capacity), capacity);
		return chunk;
	}
}

