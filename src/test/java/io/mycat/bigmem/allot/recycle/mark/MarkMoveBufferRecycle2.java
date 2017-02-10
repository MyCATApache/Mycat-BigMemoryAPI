package io.mycat.bigmem.allot.recycle.mark;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.chunkpageallot.bufferpage.impl.DirectMoveBufferPage;
import io.mycat.bigmem.chunkpageallot.recycle.impl.MarkMovePageRecycleImpl;

public class MarkMoveBufferRecycle2 {

    public static void main(String[] args) throws InterruptedException {

        DirectMoveBufferPage movePage = new DirectMoveBufferPage(new DirectMycatBufferMoveImpl(64), 2);

        MycatBufferBase buffer1 = movePage.alloactionMemory(4);

        MycatBufferBase buffer2 = movePage.alloactionMemory(4);

        buffer1.beginOp();
        buffer1.putPosition(0);
        // 重置limit容量信息
        buffer1.limit(buffer1.putPosition() );
        buffer1.commitOp();

        // 进行buffer1的内存归还操作
        movePage.recycleBuffer(buffer1,0,0);

        buffer2.beginOp();
        buffer2.putByte((byte) 21);
        buffer2.putByte((byte) 22);
        // 重新标识归还标识
        buffer2.limit(buffer2.putPosition() + 2);
        buffer2.commitOp();

        movePage.recycleBuffer(buffer2,0,0);

        printValue(movePage.getBuffer());

        MarkMovePageRecycleImpl mark = new MarkMovePageRecycleImpl();
        mark.pageClearUp(movePage);
        
        

        buffer2.beginOp();
        buffer2.putByte((byte) 23);
        buffer2.putByte((byte) 24);
        // 重新标识归还标识
        buffer2.commitOp();

        System.out.println("-----------------------------");
        System.out.println();
        System.out.println();

        printValue(movePage.getBuffer());

    }

    public static void printValue(MycatBufferBase buffer) throws InterruptedException {

        buffer.beginOp();

        buffer.getPosition(0);
        for (int i = 0; i < buffer.limit(); i++) {
            System.out.print("value:" + buffer.get() + "\t");
            if (i % 4 == 3) {
                System.out.println();
            }
        }

        System.out.println();
        System.out.println();

        buffer.commitOp();
    }

}
