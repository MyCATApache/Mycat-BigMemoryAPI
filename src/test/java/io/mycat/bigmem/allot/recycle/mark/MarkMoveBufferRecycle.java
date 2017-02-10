package io.mycat.bigmem.allot.recycle.mark;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;
import io.mycat.bigmem.chunkpageallot.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.chunkpageallot.bufferpage.impl.DirectMoveBufferPage;
import io.mycat.bigmem.chunkpageallot.recycle.impl.MarkMovePageRecycleImpl;

public class MarkMoveBufferRecycle {

    public static void main(String[] args) throws InterruptedException {

        DirectMoveBufferPage movePage = new DirectMoveBufferPage(new DirectMycatBufferMoveImpl(64), 2);

        MycatBufferBase buffer1 = movePage.alloactionMemory(4);

        MycatBufferBase buffer2 = movePage.alloactionMemory(4);

        MycatBufferBase buffer3 = movePage.alloactionMemory(4);

        buffer1.beginOp();
        buffer1.putByte((byte) 11);
        buffer1.putByte((byte) 12);
        buffer1.putByte((byte) 13);
        buffer1.putByte((byte) 14);
        // 重置limit容量信息
        buffer1.limit(buffer1.putPosition());
        buffer1.commitOp();

        // 进行buffer1的内存归还操作
        movePage.recycleBuffer(buffer1, 0, 0);

        buffer2.beginOp();
        buffer2.putByte((byte) 21);
        buffer2.putByte((byte) 22);
        // 重新标识归还标识
        buffer2.limit(buffer2.putPosition());
        buffer2.commitOp();

        movePage.recycleBuffer(buffer2, 0, 0);

        buffer3.beginOp();
        buffer3.putByte((byte) 31);
        buffer3.limit(buffer3.putPosition() + 1);
        buffer3.commitOp();

        // 回收内存
        movePage.recycleBuffer(buffer3, 0, 0);

        printValue(movePage.getBuffer());

        MarkMovePageRecycleImpl mark = new MarkMovePageRecycleImpl();
        mark.pageClearUp(movePage);

        System.out.println();
        System.out.println();
        System.out.println("-----------------------------");
        System.out.println();
        System.out.println();

        printValue(movePage.getBuffer());

        buffer3.beginOp();
        buffer3.putByte((byte) 32);
        buffer3.commitOp();

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
