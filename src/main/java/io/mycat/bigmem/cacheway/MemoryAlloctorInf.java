package io.mycat.bigmem.cacheway;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;

/**
 * 用来进行内存操作的接口
* 源文件名：CacheOperatorInf.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface MemoryAlloctorInf {

    /**
     * 进行缓存空间的分配
    * 方法描述
    * @param allocFlag 按位来进行的
    * @param size 需要的内存大小
    * @return
    * @创建日期 2016年12月20日
    */
    public MycatBufferBase allocMem(int allocFlag, int size);

    /**
     * 进行缓存空间的部分释放，即释放buffer的limit与capacity之间的空间释放
     * 方法描述
     * @param bufer
     * @return
     * @创建日期 2016年12月20日
     */
    public void recyleMem(MycatBufferBase bufer);

}
