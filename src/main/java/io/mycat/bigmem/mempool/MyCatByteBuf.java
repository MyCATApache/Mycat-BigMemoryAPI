package io.mycat.bigmem.mempool;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.MemoryAllocatorInf;

import java.nio.ByteBuffer;

/**
 * Created by znix on 2016/12/31.
 */
public class MyCatByteBuf extends MycatBufferBase {
    private MemoryAllocatorInf alloctorInf;
    private long capacity;
    private long maxCapacity;
    private long writerIndex;
    private long readerIndex;


    @Override
    public void setByte(int offset, byte value) {

    }

    @Override
    public MycatBuffer putByte(byte b) {
        return null;
    }

    @Override
    public byte getByte(int offset) {
        return 0;
    }

    @Override
    public byte get() {
        return 0;
    }

    @Override
    public void copyTo(ByteBuffer buffer) {

    }

    @Override
    public void recycleUnuse() {

    }

    @Override
    public MycatBufferBase slice() {
        return null;
    }

    @Override
    public void beginOp() {

    }

    @Override
    public void commitOp() {

    }
}
