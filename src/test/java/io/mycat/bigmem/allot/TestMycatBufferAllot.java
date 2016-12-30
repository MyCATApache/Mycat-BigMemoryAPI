package io.mycat.bigmem.allot;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.MemoryAlloctorInf;
import io.mycat.bigmem.cacheway.MycatMemoryAlloctorFactory;
import io.mycat.bigmem.console.ChunkMemoryAllotEnum;
import io.mycat.bigmem.console.MemoryAlloctorEnum;

public class TestMycatBufferAllot {

    public static void main(String[] args) {
        MemoryAlloctorInf memoryAllot = MycatMemoryAlloctorFactory
                .createMemoryAlloctor(MemoryAlloctorEnum.MEMORY_MYCAT_BUFFER_PAGE);

        // 优化使用可移动的直接内存，如果容量不够，可使用内存映射
        int allocFlag = ChunkMemoryAllotEnum.MEMORY_DIRECT_MOVE.getLevel()
                + ChunkMemoryAllotEnum.MEMORY_MAPFILE.getLevel();

        MycatBufferBase buffer = memoryAllot.allocMem(allocFlag, 1024 * 1024 * 256);

        System.out.println(buffer);
        // // 设置limit
        // buffer.limit(buffer.putPosition());
        // // 进行内存回收
        // memoryAllot.recyleMem(buffer);

        MycatBufferBase buffer2 = memoryAllot.allocMem(allocFlag, 1024 * 1024 * 64);

        System.out.println(buffer2);

        System.out.println("进行内存的分配");

    }

}
