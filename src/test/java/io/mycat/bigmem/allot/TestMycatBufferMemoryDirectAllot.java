package io.mycat.bigmem.allot;

import io.mycat.bigmem.chunkpageallot.MemoryAllocatorInf;
import io.mycat.bigmem.chunkpageallot.MycatMemoryAllocatorFactory;
import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.console.ChunkMemoryAllotEnum;

/**
 * 测试内存的可移动的分配操作
 * @author liujun
 * @date 2016年12月30日
 * @version 0.0.1
 */
public class TestMycatBufferMemoryDirectAllot {

	public static void main(String[] args) throws InterruptedException {
	    MemoryAllocatorInf memoryAllot = MycatMemoryAllocatorFactory.createMemoryAlloctor();

        // 优先使用可移动的直接内存，如果容量不够，可使用内存映射
        int allocFlag = ChunkMemoryAllotEnum.MEMORY_DIRECT.getLevel();

        // 先申请一个1k
        MycatBufferBase buffer = memoryAllot.allocMem(allocFlag, 4096 * 2);

        // 先申请一个2k
        MycatBufferBase buffer2 = memoryAllot.allocMem(allocFlag, 4096 * 2);

        buffer.beginOp();

        buffer.putByte((byte) 11);
        buffer.putByte((byte) 22);
        buffer.putByte((byte) 33);

        buffer.limit(buffer.putPosition());
        buffer.commitOp();
        // 内存归还操作
        memoryAllot.recyleMem(buffer);


	}

}
