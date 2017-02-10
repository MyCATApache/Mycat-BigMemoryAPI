package io.mycat.bigmem.chunkpageallot;

import io.mycat.bigmem.chunkpageallot.buffer.MycatBufferBase;

/**
 * 整体的内存的分配接口
 * @author liujun
 * 2016年12月29日
 */
public interface MemoryAllocatorInf {

	/**
	 * 进行缓存空间的分配
	* 方法描述
	* @param allocFlag 按位来进行的
	* @param size 需要的内存大小
	* @return
	* @创建日期 2016年12月20日
	*/
	public MycatBufferBase allocMem(int allocFlag, int size);

	/**
	 * 进行缓存空间的部分释放，即释放buffer的limit与capacity之间的空间释放
	 * 方法描述
	 * @param bufer
	 * @return
	 * @创建日期 2016年12月20日
	 */
	public void recyleMem(MycatBufferBase bufer);

}
