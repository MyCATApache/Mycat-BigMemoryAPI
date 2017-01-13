package io.mycat.bigmem.cacheway.alloctor;

import java.util.List;
import java.util.Set;

import io.mycat.bigmem.buffer.MycatBufferBase;

/**
 * 用于定义内存页的接口信息
 * @author kk
 * @date 2017年1月3日
 * @version 0.0.1
 */
public interface BufferPageMoveInf {

    /**
     * 获取分配出去的内存引用信息
     * @return
     */
    public Set<MycatBufferBase> getSliceMemory();

    /**
     * 进行目标数据的拷贝操作
     * @param bufferParam 分配的内存对象的信息
     * @param startAddress
     * @param targetAddress
     * @param limit
     */
    public void memoryCopy(MycatBufferBase useBuffer,int notUseIndex);

}
