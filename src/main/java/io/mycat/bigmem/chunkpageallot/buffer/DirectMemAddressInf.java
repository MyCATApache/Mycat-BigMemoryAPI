package io.mycat.bigmem.chunkpageallot.buffer;

/**
 * 直接内存操作接口相关的地址信息
* 源文件名：DirectMemoryAddressInf.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月23日
* 修改作者：Think
* 修改日期：2016年12月23日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface DirectMemAddressInf {

    /**
     * 获得内存地址的方法 
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    public long address();

    /**
     * 获得附着的对象
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    public Object getAttach();

}
