package io.mycat.bigmem.chunkpageallot.recycle;

import io.mycat.bigmem.chunkpageallot.bufferpage.BufferPageBase;

/**
 * 进行内存页的内存整理的接口
 * @author kk
 * 2017年1月2日
 */
public interface MemoryPageClearUpInf {

    /**
     * 进行内存页的数据整理接口定义
     * @param page  内存页信息
     */
    public void pageClearUp(BufferPageBase page);
    
    
  
}
