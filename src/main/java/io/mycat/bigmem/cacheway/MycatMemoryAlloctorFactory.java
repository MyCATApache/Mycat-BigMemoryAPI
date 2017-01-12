package io.mycat.bigmem.cacheway;

import io.mycat.bigmem.buffer.BufferAllocator;
import io.mycat.bigmem.buffer.MemoryAllocator;
import io.mycat.bigmem.console.MemoryAlloctorEnum;

/**
 * 内存分配器工厂接口
* 源文件名：MycatMemoryAlloctorFactory.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月28日
* 修改作者：liujun
* 修改日期：2016年12月28日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class MycatMemoryAlloctorFactory {

    /**
     * 创建内存分配器对象
    * 方法描述
    * @param flag 分配的标识信息
    * @return
    * @创建日期 2016年12月28日
    */
    public MemoryAlloctorInf createMemoryAlloctor(MemoryAlloctorEnum alloct) {

        // 进行对象的构建
        if (null != alloct) {
            try {
                Class<?> alloctorClass = Class.forName(alloct.getClassFile());

                MemoryAlloctorInf alloctObject = (MemoryAlloctorInf) alloctorClass.newInstance();

                return alloctObject;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

   /**
    * @see<a href="https://github.com/shenlee/Mycat-BigMemory">Mycat-BigMemory</a>
    * @return
    */
  //todo:待抽取MemoryAllocator接口类
    public MemoryAllocator getJemalloc()
    {
    	return MemoryAllocator.CURRENT;
    }
}
