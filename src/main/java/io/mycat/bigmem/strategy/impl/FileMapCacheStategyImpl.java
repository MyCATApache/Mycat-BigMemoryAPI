package io.mycat.bigmem.strategy.impl;

import java.util.Map;

import io.mycat.bigmem.strategy.CacheStrategyInf;

/**
 * 使用文件影射的缓存策略
* 源文件名：FileMapCacheStategyImpl.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class FileMapCacheStategyImpl implements CacheStrategyInf {

    @Override
    public <T> T getBufferData(Map<String, Object> p) {
        return null;
    }

    @Override
    public boolean writeBufferdata(Map<String, Object> p) {
        return false;
    }

}
