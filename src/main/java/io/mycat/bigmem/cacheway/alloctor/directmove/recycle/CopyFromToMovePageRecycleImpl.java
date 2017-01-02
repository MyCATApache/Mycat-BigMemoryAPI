package io.mycat.bigmem.cacheway.alloctor.directmove.recycle;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.alloctor.MemoryPageClearUpInf;
import io.mycat.bigmem.cacheway.alloctor.directmove.DirectMoveBufferPage;

public class CopyFromToMovePageRecycleImpl extends DirectMoveBufferPage implements MemoryPageClearUpInf {

	public CopyFromToMovePageRecycleImpl(MycatBufferBase buffer, int chunkSize) {
		super(buffer, chunkSize);
	}

	@Override
	public void pageClearUp() {

	}

}
