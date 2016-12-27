package io.mycat.bigmem.strategy;

import java.util.Map;

/**
 * 用来进行缓存的策略的接口
* 源文件名：MemcacheInf.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface CacheStrategyInf {

    /**
     * 从缓存中获取数据
    * 方法描述
    * @param p 指定的参数信息
    * @return
    * @创建日期 2016年12月20日
    */
    public <T> T getBufferData(Map<String, Object> p);

    /**
     * 进行数据写入缓存
    * 方法描述
    * @param r 写入的查询结果集信息
    * @return
    * @创建日期 2016年12月20日
    */
    public boolean writeBufferdata(Map<String, Object> p);

}
