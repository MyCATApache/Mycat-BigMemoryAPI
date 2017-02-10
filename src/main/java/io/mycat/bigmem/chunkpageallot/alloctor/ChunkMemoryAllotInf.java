package io.mycat.bigmem.chunkpageallot.alloctor;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;

/**
 * 大块的内存分配接口
 * @author liujun
 * 2016年12月29日
 */
public interface ChunkMemoryAllotInf {

    /**
     * 进行分配内在的初始化操作
     * @param memSize 内存大小
     * @param chunkSize 块内存的大小
     * @param poolSize 内存池的大小
     */
    public void allotorInit(int memSize, int chunkSize, short poolSize);

    /**
     * 进行缓存空间的分配
    * 方法描述
    * @param allocFlag 按位来进行的
    * @param size 需要的内存大小
    * @return
    * @创建日期 2016年12月20日
    */
    public MycatBufferBase allocMem(int size);

    /**
     * 进行缓存空间的部分释放，即释放buffer的limit与capacity之间的空间释放
     * 方法描述
     * @param buffer
     * @return 返回是否为当前的chunk所回收
     * @创建日期 2016年12月20日
     */
    public boolean recyleMem(MycatBufferBase buffer);

}
