package io.mycat.bigmem.chunkpageallot.console;

/**
 * buffer相关的运行时异常信息
* 源文件名：BufferException.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月26日
* 修改作者：liujun
* 修改日期：2016年12月26日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class BufferException extends RuntimeException {

    /**
    *  serialVersionUID
    */
    private static final long serialVersionUID = 1L;

    /**
     * 纯描述信息
    * 构造方法
    * @param msg 消息
    */
    public BufferException(String msg) {
        super(msg);
    }

    /**
     * 带异步信息
    * 构造方法
    * @param msg 消息
    * @param e 参数
    */
    public BufferException(String msg, Throwable e) {
        super(msg, e);
    }

}
