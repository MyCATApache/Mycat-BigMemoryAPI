package io.mycat.bigmem.console;

/**
 * 策略信息
* 源文件名：LocatePolicy.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月27日
* 修改作者：Think
* 修改日期：2016年12月27日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public enum LocatePolicy {

    /**
     * 使用本地的内存进行缓存的策略
    *  Core
    */
    Core(1),

    /**
     * 使用文件进行映射的缓存的策略信息
    *  Normal
    */
    Normal(2);

    /**
     * 策略信息
    *  policy
    */
    private int policy;

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    private LocatePolicy(int policy) {
        this.policy = policy;
    }

}
