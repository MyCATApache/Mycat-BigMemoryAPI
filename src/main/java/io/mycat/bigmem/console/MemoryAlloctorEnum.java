package io.mycat.bigmem.console;

/**
 * 内存分配的枚举信息
* 源文件名：MemoryAlloctorEnum.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月28日
* 修改作者：liujun
* 修改日期：2016年12月28日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public enum MemoryAlloctorEnum {

    /**
     * 使用内存页进行内存分配的算法
    * @字段说明 MEMORY_MYCAT_BUFFER_PAGE
    */
    MEMORY_MYCAT_BUFFER_PAGE("MYCAT_MEMORY_PAGE", "io.mycat.bigmem.cacheway.alloctor.MycatMemoryAlloctor");

    /**
     * 分配的内存标识
    * @字段说明 key
    */
    private String key;

    /**
     * 类的相关信息
    * @字段说明 classFile
    */
    private String classFile;

    private MemoryAlloctorEnum(String key, String classFile) {
        this.key = key;
        this.classFile = classFile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassFile() {
        return classFile;
    }

    public void setClassFile(String classFile) {
        this.classFile = classFile;
    }
    
    
    

}
