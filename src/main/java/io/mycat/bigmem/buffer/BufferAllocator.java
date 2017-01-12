package io.mycat.bigmem.buffer;


/**
 * allocator , recycle and gc buffers
 * @author shenli
 *
 */
public interface BufferAllocator {

	public DirectByteBuffer directBuffer(int capacity);
	
    public void recycle(DirectByteBuffer theBuf) ;
}
