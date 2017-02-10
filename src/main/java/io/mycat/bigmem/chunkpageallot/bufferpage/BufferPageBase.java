package io.mycat.bigmem.chunkpageallot.bufferpage;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;

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

    /**
    * 是否锁定标识
    * @字段说明 isLock
    */
    protected AtomicBoolean isLock = new AtomicBoolean(false);

    /**
     * 可以使用的chunkNum
     * @字段说明 useMemoryChunkNum
     */
    protected int canUseChunkNum;

    public BufferPageBase(MycatBufferBase buffer, int chunkSize) {
        this.buffer = buffer;
        // 设置chunk的大小
        this.chunkSize = chunkSize;
        // 设置chunk的数量
        this.chunkCount = (int) buffer.limit() / this.chunkSize;
        // 设置当前内存标识块的大小
        this.memUseSet = new BitSet(this.chunkCount);
        // 默认可使用的chunk数量为总的chunk数
        this.canUseChunkNum = chunkCount;
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

    /**
     * 需要实现的，能够计算可用chunk的方法
     * @param chunkNum
     * @return
     */
    public abstract boolean checkNeedChunk(int chunkNum);

    /**
     * 进行指定内存块分配的方法
     * @param needChunkSize 需要的内存页数大小
     * @return
     */
    public abstract MycatBufferBase alloactionMemory(int needChunkSize);

    /**
     * 进宪内存归还的方法
     * @param parentBuffer 分配的内存对象
     * @param chunkStart 开始的内存块索引号
     * @param chunkNum 归还的数量
     * @return
     */
    public abstract boolean recycleBuffer(MycatBufferBase parentBuffer, int chunkStart, int chunkNum);

}
