package io.mycat.bigmem.allot.recycle;

import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.buffer.impl.DirectMycatBufferMoveImpl;
import io.mycat.bigmem.cacheway.alloctor.directmove.DirectMoveBufferPage;

public class MoveBufferRecycle {

	public static void main(String[] args) {

		DirectMoveBufferPage movePage = new DirectMoveBufferPage(new DirectMycatBufferMoveImpl(64), 2);

		MycatBufferBase buffer1 = movePage.alloactionMemory(4);

		MycatBufferBase buffer2 = movePage.alloactionMemory(4);

		MycatBufferBase buffer3 = movePage.alloactionMemory(4);

		buffer1.beginOp();
		buffer1.putByte((byte) 1);
		buffer1.putByte((byte) 1);
		buffer1.putByte((byte) 1);
		buffer1.putByte((byte) 1);
		buffer1.commitOp();

		// 进行buffer1的内存归还操作
		movePage.recycleBuffer((MycatBufferBase) buffer1.getAttach(), 2, 2);

		buffer2.beginOp();
		buffer2.putByte((byte) 2);
		buffer2.putByte((byte) 2);
		buffer2.commitOp();

		movePage.recycleBuffer((MycatBufferBase) buffer1.getAttach(), 5, 3);

	}

}
