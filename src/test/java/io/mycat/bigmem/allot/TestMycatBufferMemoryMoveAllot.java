package io.mycat.bigmem.allot;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.cacheway.MemoryAlloctorInf;
import io.mycat.bigmem.cacheway.MycatMemoryAlloctorFactory;
import io.mycat.bigmem.console.ChunkMemoryAllotEnum;

/**
 * 测试内存的可移动的分配操作
 * @author liujun
 * @date 2016年12月30日
 * @version 0.0.1
 */
public class TestMycatBufferMemoryMoveAllot {

    public static void main(String[] args) {
        MemoryAlloctorInf memoryAllot = MycatMemoryAlloctorFactory.createMemoryAlloctor();

        // 优先使用可移动的直接内存，如果容量不够，可使用内存映射
        int allocFlag = ChunkMemoryAllotEnum.MEMORY_DIRECT_MOVE.getLevel();

        MycatBufferBase buffer = memoryAllot.allocMem(allocFlag, 1024 * 1024 * 1);

        System.out.println(buffer);

        MycatBufferBase buffer2 = memoryAllot.allocMem(allocFlag, 1024 * 1024 * 1);

        // 开始操作内存
        buffer2.beginOp();

        // 填充数据
        buffer2.putByte((byte) 1);

        buffer2.limit(buffer2.putPosition());
        // 内存操作完毕
        buffer2.commitOp();

        // 进行内存的归还操作
        memoryAllot.recyleMem(buffer2);

        System.out.println(buffer2);

    }

}
