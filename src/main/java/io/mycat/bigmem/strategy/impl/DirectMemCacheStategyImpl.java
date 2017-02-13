package io.mycat.bigmem.strategy.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.mycat.bigmem.strategy.CacheStrategyInf;

/**
 * 使用内存进行缓存的策略
* 源文件名：DirectMemCacheStategyImpl.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class DirectMemCacheStategyImpl implements CacheStrategyInf {

    /**
     * 进行缓存key与对应的内存的块的信息
    *  cahceMap
    */
    private Map<String, Object> cahceMap = new ConcurrentHashMap<>();

    @Override
    public <T> T getBufferData(Map<String, Object> p) {

        // 1,根据key从缓存中提取内存数据

        // 2,检查当前是否缓存是否在有效期内

        // 3,在有效期内，将结果返回

        return null;
    }

    @Override
    public boolean writeBufferdata(Map<String, Object> p) {

        // 1,获得数据信息
        // 2,计算填充数据的大小
        // 3,申请缓存的数据空间
        // 4,将数据装入缓存
        // 5,将缓存的数据放入map中
        // 6,返回结果
        return false;
    }

}
