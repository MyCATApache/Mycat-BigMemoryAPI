package io.mycat.bigmem.cacheway;

import io.mycat.bigmem.buffer.MemoryAllocator;
import io.mycat.bigmem.console.PropertiesKeyEnum;
import io.mycat.bigmem.util.PropertiesUtils;

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
public class MycatMemoryAllocatorFactory {

    /**
     * 创建内存分配器对象
    * 方法描述
    * @param flag 分配的标识信息
    * @return
    * @创建日期 2016年12月28日
    */
    public static MemoryAllocatorInf createMemoryAlloctor() {

        try {
            // 获得内存分配的配制信息
            String allotClass = PropertiesUtils.getInstance()
                    .getValue(PropertiesKeyEnum.MYCAT_MEMORY_ALLOT_CLASS.getKey());
            // 加载文件信息
            Class<?> alloctorClass = Class.forName(allotClass);

            // 生成对象
            MemoryAllocatorInf alloctObject = (MemoryAllocatorInf) alloctorClass.newInstance();

            return alloctObject;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @see<a href="https://github.com/shenlee/Mycat-BigMemory">Mycat-BigMemory</a>
     * @return
     * @author shenli, zwyqz, tracywwp
     */
   //todo:待抽取MemoryAllocator接口类
     public MemoryAllocator getJemalloc()
     {
     	return MemoryAllocator.CURRENT;
     }
}
