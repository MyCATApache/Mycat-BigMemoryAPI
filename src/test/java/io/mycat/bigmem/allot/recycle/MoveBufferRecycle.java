package io.mycat.bigmem.allot.recycle;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.chunkpageallot.bufferpage.impl.DirectMoveBufferPage;

public class MoveBufferRecycle {

    public static void main(String[] args) throws InterruptedException {

        DirectMoveBufferPage movePage = new DirectMoveBufferPage(new DirectMycatBufferMoveImpl(64), 2);

        MycatBufferBase buffer1 = movePage.alloactionMemory(2);

        MycatBufferBase buffer2 = movePage.alloactionMemory(4);

        MycatBufferBase buffer3 = movePage.alloactionMemory(4);

        buffer1.beginOp();
        buffer1.putByte((byte) 1);
        buffer1.putByte((byte) 1);
        buffer1.putByte((byte) 1);
        buffer1.putByte((byte) 1);
        buffer1.putByte((byte) 1);
        buffer1.commitOp();

        // 进行buffer1的内存归还操作
        movePage.recycleBuffer(buffer1,0,1);

        buffer2.beginOp();
        buffer2.putByte((byte) 2);
        buffer2.putByte((byte) 2);
        buffer2.commitOp();

        movePage.recycleBuffer(buffer1,0,0);

    }
    
    

}
