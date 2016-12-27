package io.mycat.bigmem.strategy;

import java.util.HashMap;
import java.util.Map;

import io.mycat.bigmem.console.LocatePolicy;
import io.mycat.bigmem.strategy.impl.DirectMemCacheStategyImpl;
import io.mycat.bigmem.strategy.impl.FileMapCacheStategyImpl;

/**
 * 缓存的策略信息
* 源文件名：CacheStrategy.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月20日
* 修改作者：liujun
* 修改日期：2016年12月20日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class CacheStrategy {

    /**
     * 缓存策略的信息存储的Map
    * @字段说明 strategyMap
    */
    private static final Map<LocatePolicy, CacheStrategyInf> strategyMap = new HashMap<>();

    static {
        // 使用直接内存进行缓存的策略
        strategyMap.put(LocatePolicy.Core, new DirectMemCacheStategyImpl());
        // 使用文件映射的方式进行缓存的策略
        strategyMap.put(LocatePolicy.Normal, new FileMapCacheStategyImpl());
    }

    private static final CacheStrategy INSTANCE = new CacheStrategy();

    private CacheStrategy() {

    }

    public static CacheStrategy getInstance() {
        return INSTANCE;
    }

    /**
     * 获得策略信息
    * 方法描述
    * @param policy
    * @return
    * @创建日期 2016年12月20日
    */
    public CacheStrategyInf getStrategy(LocatePolicy policy) {
        return strategyMap.get(policy);
    }

}
