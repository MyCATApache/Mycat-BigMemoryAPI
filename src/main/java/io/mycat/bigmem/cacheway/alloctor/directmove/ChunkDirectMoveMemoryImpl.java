package io.mycat.bigmem.cacheway.alloctor.directmove;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.cacheway.alloctor.ChunkMemoryAllotInf;

/**
 * 进行直接可移动的大内存的分配操作
 * @author liujun
 * 2016年12月29日
 */
public class ChunkDirectMoveMemoryImpl implements ChunkMemoryAllotInf {

	/**
	 * 内存池对象信息
	 * @字段说明 pool
	 */
	private DirectMoveBufferPage[] POOL;

	/**
	* 每个chunk的大小
	* @字段说明 CHUNK_SIZE
	*/
	private int CHUNK_SIZE;

	/**
	 * 初始化标识
	 */
	private boolean initFlag;

	@Override
	public void allotorInit(int memSize, int chunkSize, short poolSize) {
		CHUNK_SIZE = chunkSize;
		// 进行每个内存页的初始化
		POOL = new DirectMoveBufferPage[poolSize];
		// 进行每个chunk的页面的分配内存操作
		for (int i = 0; i < poolSize; i++) {
			POOL[i] = new DirectMoveBufferPage(new DirectMycatBufferMoveImpl(memSize), CHUNK_SIZE);
		}
		// 标识初始化完成
		initFlag = true;
	}

	@Override
	public MycatBufferBase allocMem(int size) {

		if (!initFlag) {
			throw new RuntimeException("ChunkDirectMemoryImpl memory not init ,please invoke allotorInit");
		}

		// 计算需要的chunk大小
		int needChunk = size % CHUNK_SIZE == 0 ? size / CHUNK_SIZE : size / CHUNK_SIZE + 1;
		// 取得内存页信息
		DirectMoveBufferPage page = null;
		for (DirectMoveBufferPage pageMemory : POOL) {
			if (pageMemory.checkNeedChunk(needChunk)) {
				page = pageMemory;
				break;
			}
		}

		// 如果能找合适的内存空间，则进行分配
		if (null != page) {
			// 针对当前的chunk进行内存的分配操作
			MycatBufferBase buffer = page.alloactionMemory(needChunk);
			return buffer;
		}
		return null;
	}

	@Override
	public boolean recyleMem(MycatBufferBase buffer) {
		// 验证当前的buffer再进行回收
		if (null != buffer && buffer instanceof DirectMycatBufferMoveImpl) {

			if (buffer.limit() < buffer.capacity()) {

				boolean recyProc = false;

				for (DirectMoveBufferPage pageMemory : POOL) {
					if ((recyProc = pageMemory.recycleBuffer(buffer)) == true) {

						break;
					}
				}

				if (!recyProc) {
					System.out.println("memory recycle fail");
				}
			} else {
				System.out.println("not memory recycle");
			}

			return true;
		}

		return false;
	}

}
