package io.mycat.bigmem.mempool;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.MemoryAllocatorInf;

/**
 * Created by znix on 2016/12/31.
 */
public class MyCatMemAlloctorImp implements MemoryAllocatorInf{
    @Override
    public MycatBufferBase allocMem(int allocFlag, int size) {
        return null;
    }

    @Override
    public void recyleMem(MycatBufferBase bufer) {

    }
}
