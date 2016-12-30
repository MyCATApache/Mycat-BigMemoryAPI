package io.mycat.bigmem.cacheway.alloctor;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.MemoryAlloctorInf;
import io.mycat.bigmem.cacheway.MemoryAllotRestult;
import io.mycat.bigmem.console.ChunkMemoryAllotEnum;

/**
 * java 内存池的实现
 * 源文件名：MemoryPool.java
 * 文件版本：1.0.0
 * 创建作者：liujun
 * 创建日期：2016年12月19日
 * 修改作者：liujun
 * 修改日期：2016年12月19日
 * 文件描述：TODO
 * 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
 */
public class MycatMemoryAlloctor implements MemoryAlloctorInf {

	/**
	 * 存储级别的map
	 */
	private static Set<ChunkMemoryAllotEnum> MEMORY_LEVEL = new TreeSet<>((o1, o2) -> {
		if (o1.getLevel() < o2.getLevel()) {
			return 1;
		} else if (o1.getLevel() > o2.getLevel()) {
			return -1;
		}
		return 0;
	});

	/**
	* 用来构建内存池对象信息
	* 构造方法
	* @param chunkSize
	* @param memorySize
	* @param poolSize
	 * @throws IOException 
	*/
	public MycatMemoryAlloctor() throws IOException {
		int msize = 1024 * 1024;

		for (int i = 0; i < ChunkMemoryAllotEnum.values().length; i++) {
			MEMORY_LEVEL.add(ChunkMemoryAllotEnum.values()[i]);
		}

		// 构建文件映射的相关初始化
		ChunkMemoryAllotEnum.MEMORY_MAPFILE.getChunkAllot().allotorInit(msize * 128, 4096, (short) 1);
		// 可移动的内存文件块的构建
		ChunkMemoryAllotEnum.MEMORY_DIRECT_MOVE.getChunkAllot().allotorInit(msize * 256, 4096, (short) 1);
		// 进行不可移动的内存块的构建
		ChunkMemoryAllotEnum.MEMORY_DIRECT.getChunkAllot().allotorInit(msize * 16, 4096, (short) 1);
	}

	/**
	 * 获取最高级别，也就是最先匹配的级别
	 * @param allocFlag
	 * @param minlevel
	 * @return
	 */
	private ChunkMemoryAllotEnum memoryMatchlevel(int allocFlag, int index) {

		ChunkMemoryAllotEnum chunkAllot = null;

		for (ChunkMemoryAllotEnum chunkMemoryAllotEnum : MEMORY_LEVEL) {
			// 首次需要匹配上最优的内存内存操作
			if (index == 0) {
				// 检查是否有当前的级别
				if ((chunkMemoryAllotEnum.getLevel() & allocFlag) == chunkMemoryAllotEnum.getLevel()) {
					chunkAllot = chunkMemoryAllotEnum;
					index = chunkMemoryAllotEnum.getMoveBit();
					break;
				}
			} else {
				// 按级别选择最优的内存
				if (chunkMemoryAllotEnum.getMoveBit() < index
						&& (chunkMemoryAllotEnum.getLevel() & allocFlag) == chunkMemoryAllotEnum.getLevel()) {
					chunkAllot = chunkMemoryAllotEnum;
					index = chunkMemoryAllotEnum.getMoveBit();
				}
			}
		}

		return chunkAllot;
	}

	/**
	* 进行内存分配操作
	* 方法描述
	* @param size 需要的内存大小,最好CHUNK_SIZE的倍数，以方便 内存的回收利用
	* @return
	* @创建日期 2016年12月19日
	*/
	public MemoryAllotRestult allocMem(int allocFlag, int size) {

		MemoryAllotRestult allotResult = null;

		MycatBufferBase mycatBuffer = null;

		// 首次为0匹配上最想要的内存
		int index = 0;

		ChunkMemoryAllotEnum allot = null;
		while ((allot = this.memoryMatchlevel(allocFlag, index)) != null) {
			// 进行内存分配
			mycatBuffer = allot.getChunkAllot().allocMem(size);
			if (mycatBuffer == null) {
				continue;
			} else {
				break;
			}
		}

		// 如果内存分配成功，则记录下当前的分配信息
		if (null != mycatBuffer) {
			allotResult = new MemoryAllotRestult(mycatBuffer, allot);
		}

		return allotResult;
	}

	/**
	* 进行内存的归还操作
	* 方法描述
	* @param buffer
	* @创建日期 2016年12月19日
	*/
	public void recyleMem(MemoryAllotRestult buffer) {
		if (null != buffer) {
			// 进行内存的回收操作
			buffer.getMemoryAllotEnum().getChunkAllot().recyleMem(buffer.getMycatBuffer());
		}
	}

}
