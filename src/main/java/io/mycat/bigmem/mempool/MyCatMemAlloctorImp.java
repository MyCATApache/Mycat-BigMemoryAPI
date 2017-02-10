package io.mycat.bigmem.mempool;

import io.mycat.bigmem.chunkpageallot.MemoryAllocatorInf;
import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;

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
