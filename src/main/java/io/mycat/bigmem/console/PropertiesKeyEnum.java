package io.mycat.bigmem.console;

/**
 * 配制文件的key的信息
 * or liujun
 *  2016年12月30日
 * @version 0.0.1
 */
public enum PropertiesKeyEnum {
    
    /**
     * 进行内存分配算法的默认的实现
     */
    MYCAT_MEMORY_ALLOT_CLASS("mycat.memory.allot.class","");


    /**
     * 分配的内存标识
    *  key
    */
    private String key;

    /**
     * 类的相关信息
    *  classFile
    */
    private String value;
    
    

    private PropertiesKeyEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
}
