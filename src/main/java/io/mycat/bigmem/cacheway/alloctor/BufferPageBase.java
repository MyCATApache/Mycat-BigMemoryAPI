package io.mycat.bigmem.cacheway.alloctor;

import java.util.BitSet;

import io.mycat.bigmem.buffer.MycatBufferBase;

/**
 * 进行内存页数据的相关的抽象
 * @author kk
 * @date 2017年1月3日
 * @version 0.0.1
 */
public abstract class BufferPageBase {

    /**
     * 操作的buffer信息
    * @字段说明 buffer
    */
    protected MycatBufferBase buffer;

    /**
    * 每个chunk的大小
    * @字段说明 chunkSize
    */
    protected int chunkSize;

    /**
    * 总的chunk数
    * @字段说明 chunkIndex
    */
    protected int chunkCount;

    /**
    * 用于标识内存是否使用集合
    * @字段说明 memUseSet
    */
    protected final BitSet memUseSet;

    public BufferPageBase(MycatBufferBase buffer, int chunkSize) {
        this.buffer = buffer;
        // 设置chunk的大小
        this.chunkSize = chunkSize;
        // 设置chunk的数量
        this.chunkCount = (int) buffer.limit() / this.chunkSize;
        // 设置当前内存标识块的大小
        this.memUseSet = new BitSet(this.chunkCount);
    }

    public MycatBufferBase getBuffer() {
        return buffer;
    }

    public void setBuffer(MycatBufferBase buffer) {
        this.buffer = buffer;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public BitSet getMemUseSet() {
        return memUseSet;
    }

}
