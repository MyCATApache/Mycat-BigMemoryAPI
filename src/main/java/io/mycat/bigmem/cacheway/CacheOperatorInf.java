package io.mycat.bigmem.cacheway;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;

/**
 * 用来进行缓存操作的接口
* 源文件名：CacheOperatorInf.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface CacheOperatorInf {

    /**
     * 进行缓存空间的分配
    * 方法描述
    * @param size 需要的内存大小
    * @param timeout 过期的时间
    * @return
    * @创建日期 2016年12月20日
    */
    public MycatBuffer allocationMemory(int size, long timeOut);

    /**
     * 进行缓存空间的全部释放
    * 方法描述
    * @param bufer
    * @return
    * @创建日期 2016年12月20日
    */
    public boolean recycleAll(MycatBufferBase bufer);

    /**
     * 进行缓存空间的部分释放，即释放buffer的limit与capacity之间的空间释放
     * 方法描述
     * @param bufer
     * @return
     * @创建日期 2016年12月20日
     */
    public boolean recycleNotUse(MycatBufferBase bufer);

}
