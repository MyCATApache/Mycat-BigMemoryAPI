package io.mycat.bigmem.buffer;

/**
 * 进行回调的相关的接口定义
* 源文件名：MyCatCallbackInf.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月22日
* 修改作者：Think
* 修改日期：2016年12月22日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface CatCallbackInf {

    /**
     * 进行异步的通知
    * 方法描述
    * @throws Exception
    *  2016年12月26日
    */
    public void callBack() throws Exception;

}
