package io.mycat.bigmem.buffer;

import java.io.IOException;

import io.mycat.bigmem.buffer.impl.DirectMycatBufferImpl;
import io.mycat.bigmem.buffer.impl.MapFileBufferImp;
import io.mycat.bigmem.console.LocatePolicy;

/**
 * 基本的buffer的父类信息
* 源文件名：MycatBufferBase.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月27日
* 修改作者：Think
* 修改日期：2016年12月27日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public abstract class MycatBufferBase implements MycatBuffer, DirectMemAddressInf {

	/**
	 * 当前写入的指针位置
	* @字段说明 position
	*/
	protected int putPosition;

	/**
	 * 当前读取指针的位置
	* @字段说明 getPosition
	*/
	protected int getPosition;

	/**
	 * 当前的的容量
	* @字段说明 limit
	*/
	protected int limit;

	/**
	 * 容量信息
	* @字段说明 capacity
	*/
	protected int capacity;

	/**
	 * 内存映射地址信息,在被交换后可以更改
	* @字段说明 addr
	*/
	protected long address;

	/**
	 * 当前附着的对象
	* @字段说明 att
	*/
	protected Object att;

	/**
	 * 获取当前的读取指针
	* 方法描述
	* @return
	* @创建日期 2016年12月27日
	*/
	public int getPosition() {
		return this.getPosition;
	}

	/**
	 * 设置当前的读取指针
	* 方法描述
	* @param getPosition
	* @创建日期 2016年12月27日
	*/
	public void getPosition(int getPosition) {
		this.getPosition = getPosition;
	}

	/**
	 * 获取当前的容量
	* 方法描述
	* @return
	* @创建日期 2016年12月27日
	*/
	public int limit() {
		return this.limit;
	}

	/**
	 * 设置当前的容量
	* 方法描述
	* @param limit
	* @创建日期 2016年12月27日
	*/
	public void limit(int limit) {
		this.limit = limit;
	}

	/**
	 * 获取写入的指针
	* 方法描述
	* @return
	* @创建日期 2016年12月27日
	*/
	public int putPosition() {
		return this.putPosition;
	}

	/**
	 * 设置写入的指针
	* 方法描述
	* @param position
	* @创建日期 2016年12月27日
	*/
	public void putPosition(int position) {
		this.putPosition = position;
	}

	/**
	 * 获取容量信息
	* 方法描述
	* @return
	* @创建日期 2016年12月27日
	*/
	public int capacity() {
		return this.capacity;
	}

	/**
	 * 获取容量信息
	 * 方法描述
	 * @return
	 * @创建日期 2016年12月27日
	 */
	public void capacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * 根据指定的策略信息以获取相应的容量信息
	* 方法描述
	* @param capcation
	* @return
	 * @throws IOException 
	* @创建日期 2016年12月27日
	*/
	public static MycatBufferBase getMyCatBuffer(LocatePolicy policy, int capcation) throws IOException {
		// 检查是否使用直接内存进行缓存
		if (policy.equals(LocatePolicy.Core)) {
			return new DirectMycatBufferImpl(capcation);
		} else if (policy.equals(LocatePolicy.Normal)) {
			return new MapFileBufferImp(capcation);
		}

		return null;
	}

	/**
	 * 获取地址信息
	 */
	public long address() {
		return this.address;
	}

	/**
	 * 获取附着的信息
	 */
	public Object getAttach() {
		return this.att;
	}

	/**
	 * 内存可以被管理器移动，首次访问（一个begin & commit操作之后）之前，
	 * 用户需要先要调用beginOp()，即完整用法如下   
	 *  Buf.beginOp();   
	 *   Read or write    
	 *   Buf.commitOp();    
	 */
	public abstract void beginOp();

	/**
	 * 当前操作完成，事要进行内存整理
	 */
	public abstract void commitOp();

}
