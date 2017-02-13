package io.mycat.bigmem.chunkpageallot.console;

import io.mycat.bigmem.chunkpageallot.alloctor.ChunkMemoryAllotInf;
import io.mycat.bigmem.chunkpageallot.alloctor.impl.ChunkDirectMemoryImpl;
import io.mycat.bigmem.chunkpageallot.alloctor.impl.ChunkDirectMoveMemoryImpl;
import io.mycat.bigmem.chunkpageallot.alloctor.impl.ChunkFileMapMemoryImpl;

/**
 * 进行大内存分配的级别对象信息
 * or liujun
 * 2016年12月29日
 */
public enum ChunkMemoryAllotEnum {

    /**
     * 直接内存分配操作,不可移动的内存
     */
    MEMORY_DIRECT(1 << 2, 2, new ChunkDirectMemoryImpl()),

    /**
     * 可移动的内存操作
     */
    MEMORY_DIRECT_MOVE(1 << 1, 1, new ChunkDirectMoveMemoryImpl()),

    /**
     * 内存映射文件操作
     */
    MEMORY_MAPFILE(1, 0, new ChunkFileMapMemoryImpl());

    /**
     * 当前的级别信息
     */
    private int level;

    /**
     * 移动的位数
     */
    private int moveBit;

    private ChunkMemoryAllotInf chunkAllot;

    private ChunkMemoryAllotEnum(int level, int moveBit, ChunkMemoryAllotInf chunkAllot) {
        this.level = level;
        this.moveBit = moveBit;
        this.chunkAllot = chunkAllot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ChunkMemoryAllotInf getChunkAllot() {
        return chunkAllot;
    }

    public void setChunkAllot(ChunkMemoryAllotInf chunkAllot) {
        this.chunkAllot = chunkAllot;
    }

    public int getMoveBit() {
        return moveBit;
    }

    public void setMoveBit(int moveBit) {
        this.moveBit = moveBit;
    }

}
