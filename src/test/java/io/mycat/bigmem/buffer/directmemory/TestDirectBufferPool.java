package io.mycat.bigmem.buffer.directmemory;

import java.io.IOException;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.alloctor.MycatMemoryAlloctor;
import io.mycat.bigmem.console.LocatePolicy;

public class TestDirectBufferPool {

    public static void main(String[] args) throws IOException {

        // 构建内存池对象
        MycatMemoryAlloctor poolBuffer = new MycatMemoryAlloctor(LocatePolicy.Core, 2048, 128, (short) 1);

        // 进行内存的申请
        MycatBufferBase buffer = poolBuffer.allocationMemory(1024, System.currentTimeMillis());

        buffer.beginOp();

        // 进行内存数据卦
        fillValue(buffer);

        printValue(buffer);

        // 进行内存的回收
        // 测试全部回收
        // 回收内存之前，需要设置limit的大小，以确定回收的内存大小
        buffer.limit(256);
        poolBuffer.recycleNotUse(buffer);

        // 测试内存归还后，是否可继续申请
        MycatBufferBase buffer2 = poolBuffer.allocationMemory(1792, System.currentTimeMillis());

        System.out.println("内存空间大小:" + buffer2.limit());

        buffer.commitOp();
    }

    /**
     * 进行内存的填充
    * 方法描述
    * @param buffer
     * @throws IOException 
    * @创建日期 2016年12月23日
    */
    public static void fillValue(MycatBufferBase buffer) throws IOException {
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.putByte((byte) i);
        }
    }

    public static void printValue(MycatBufferBase buffer) {
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.print("curr value:" + buffer.get() + "\t");
            if (i % 4 == 0) {
                System.out.println();
            }
        }
        
        System.out.println();
        System.out.println();
    }

}
