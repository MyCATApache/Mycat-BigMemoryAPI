package io.mycat.bigmem.cacheway.alloctor;

/**
 * 进行内存页的内存整理的接口
 * @author kk
 * 2017年1月2日
 */
public interface MemoryPageClearUpInf {

	/**
	 * 进行内存页的数据整理接口定义
	 */
	public void pageClearUp();
}
