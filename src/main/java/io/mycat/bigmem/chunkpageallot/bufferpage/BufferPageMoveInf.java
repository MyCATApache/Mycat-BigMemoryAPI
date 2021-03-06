package io.mycat.bigmem.chunkpageallot.bufferpage;

import java.util.Set;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;

/**
 * 用于定义内存页的接口信息
 * @author kk
 * @version 0.0.1
 */
public interface BufferPageMoveInf {

    /**
     * 获取分配出去的内存引用信息
     * @return 返回 
     */
    public Set<MycatBufferBase> getSliceMemory();

    /**
     * 进行目标数据的拷贝操作
     * @param useBuffer 分配的内存对象的信息 
     * @param notUseIndex 索引
     */
    public void memoryCopy(MycatBufferBase useBuffer,int notUseIndex);

}
