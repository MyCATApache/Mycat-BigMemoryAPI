package io.mycat.bigmem.buffer;

import io.mycat.bigmem.util.UnsafeUtil;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements of DirectByteBuffer should completely implement
 * java.nio.ByteBuffer
 * 
 * @author shenli
 *
 */

public class DirectByteBuffer extends BaseByteBuffer<ByteBuffer> {

	final static Logger LOGGER = LoggerFactory
			.getLogger(DirectByteBuffer.class);

	public static BaseByteBuffer<ByteBuffer> newInstance(int cap) {
		BaseByteBuffer<ByteBuffer> byteBuffer = new DirectByteBuffer(cap);
		return byteBuffer;
	}

	/**
	 * 
	 */
	private DirectByteBuffer(int cap) {
		super(0, 0, 0, cap);
	}

	@Override
	public DirectByteBuffer init(Chunk<ByteBuffer> chunk, long handle,
			int offset, int length, int maxLength) {
		super.init(chunk, handle, offset, length, maxLength);
		return this;
	}

	@Override
	public DirectByteBuffer init(Chunk<ByteBuffer> chunks[],
			long handleInLastChunk, int offsetInLastChunk,
			int lengthInLastChunk, int maxLengthInLastChunk) {
		super.init(chunks, handleInLastChunk, offsetInLastChunk,
				lengthInLastChunk, maxLengthInLastChunk);
		return this;
	}

	/**
	 * @desc
	 * @auth zhangwy @date 2017年1月2日 下午5:33:51
	 **/
	private long chunkAddr(Chunk<ByteBuffer> chunk) {
		Field field;
		long memoryAddress = 0;
		try {
			field = Buffer.class.getDeclaredField("address");
			field.setAccessible(true);
			long fieldOffset = UnsafeUtil.getUnsafe().objectFieldOffset(field);
			memoryAddress = UnsafeUtil.getUnsafe().getLong(chunk.getMemory(),
					fieldOffset);
		} catch (NoSuchFieldException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return memoryAddress;
	}

	/**
	 * 获取position在chunkList内的累加过的基础Offset
	 * 
	 * @param chunkIndex
	 * @return
	 */
	private int getChunkBaseOffset(int chunkIndex) {
		int offset = 0;
		for (int i = 0; i < chunkIndex; i++) {
			offset += chunkList.get(i).getArena().chunkSize;
		}
		if (chunkIndex == chunkList.size() - 1) { // 如果所在的chunk是数组的最后一个chunk
			offset += this.offsetInLastChunk;
		}
		return offset;
	}

	private int getChunkEndOffset(int chunkIndex, int chunkBaseOffset) {
		int offset = 0;
		if (chunkIndex == chunkList.size() - 1) { // 如果所在的chunk是数组的最后一个chunk
			offset += chunkBaseOffset + this.lengthInLastChunk;
		} else {
			offset += chunkBaseOffset
					+ chunkList.get(chunkIndex).getArena().chunkSize;
		}
		return offset;
	}

	private int findChunkIndex(int position) {
		if (chunkList.size() == 0) {
			return -1;
		}
		Chunk<ByteBuffer> c = chunkList.get(0);
		return position / c.getArena().chunkSize;
		// for (int low = 0, high = chunkList.size(); low <= high;) {
		// int mid = low + high >>> 1;
		// Chunk<ByteBuffer> c = chunkList.get(mid);
		// if (position >= c.getArena().chunkSize * mid) {
		// low = mid + 1;
		// } else if (position < c.getArena().chunkSize * mid) {
		// high = mid - 1;
		// } else {
		// return mid;
		// }
		// }
		// throw new Error("should not reach here");
	}

	public long memoryAddress(int index) {
		long addr = 0l;
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		addr = chunkAddr(c) + (index - baseOffset);
		return addr;
	}

	/**
	 * 
	 */
	@Override
	public BaseByteBuffer<ByteBuffer> slice() {
//        int pos = this.position();
//        int lim = this.limit();
//        assert (pos <= lim);
//        int rem = (pos <= lim ? lim - pos : 0); //剩余
//        int off = (pos << 0);
//        assert (off >= 0);
		return null;
	}

	@Override
	public BaseByteBuffer<ByteBuffer> duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseByteBuffer<ByteBuffer> asReadOnlyBuffer() {

		return null;
	}

	@Override
	public byte get() {
		return _get(nextGetIndex());
	}

	@Override
	public byte get(int index) {
		return _get(checkIndex(index));
	}

	public BaseByteBuffer<ByteBuffer> get(byte[] dst, int offset, int length) {
		// todo:此处算法待优化
		return super.get(dst, offset, length);
	}

	public BaseByteBuffer<ByteBuffer> get(byte[] dst) {
		// todo:此处算法待优化
		return super.get(dst);
	}

	@Override
	byte _get(int index) {
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		return c.getMemory().get(index - baseOffset);
	}

	/**
	 * 基本逻辑同 byte _get(int index)
	 */
	@Override
	void _put(int index, byte b) {
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		c.getMemory().put(index - baseOffset, b);
	}

	@Override
	public BaseByteBuffer<ByteBuffer> put(byte b) {
		_put(nextPutIndex(), b);
		return this;
	}

	@Override
	public BaseByteBuffer<ByteBuffer> put(int index, byte b) {
		_put(checkIndex(index), b);
		return this;
	}

	@Override
	public boolean isDirect() {
		return true;
	}

	public BaseByteBuffer<ByteBuffer> put(BaseByteBuffer src) {
		// todo:此处算法待优化
		return super.put(src);
	}

	public BaseByteBuffer<ByteBuffer> put(byte[] src, int offset, int length) {
		// todo:此处算法待优化
		return super.put(src, offset, length);
	}

	@Override
	public short getShort() {
		return getShort(nextGetIndex(1 << 1));
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putShort(short value) {
		// TODO Auto-generated method stub
		return putShort(nextPutIndex(1 << 1), value);
	}

	@Override
	public short getShort(int index) {
		checkIndex(index, 1<<1);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
		if (index + 2 <= endOffset) {
			return c.getMemory().getShort(index - baseOffset);
		} else if (this.bigEndian) {
			return (short) ((_get(index) & 0xff) << 8 | _get(index + 1) & 0xff);
		} else {
			return (short) (_get(index) & 0xff | (_get(index + 1) & 0xff) << 8);
		}
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putShort(int index, short value) {
		checkIndex(index, 1<<1);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
		if (index + 2 <= endOffset) {
			c.getMemory().putShort(index - baseOffset, value);
		} else if (this.bigEndian) {
			_put(index, (byte) (value >>> 8));
			_put(index + 1, (byte) value);
		} else {
			_put(index, (byte) value);
			_put(index + 1, (byte) (value >>> 8));
		}
		return this;
	}

	@Override
	public char getChar() {
		return (char)getShort();
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putChar(char value) {
		return putShort((short)value);
	}

	@Override
	public char getChar(int index) {
		return (char)getShort(index);
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putChar(int index, char value) {
		return putShort(index, (short)value);
	}

	@Override
	public int getInt() {
		return getInt(nextGetIndex(1 << 2));
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putInt(int value) {
		return putInt(nextPutIndex(1 << 2), value);
	}

	@Override
	public int getInt(int index) {
		checkIndex(index, 1<<2);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
		if (index + 4 <= endOffset) {
			return c.getMemory().getInt(index - baseOffset);
		} else if (this.bigEndian) {
			  return (getShort(index) & 0xffff) << 16 | getShort(index + 2) & 0xffff;
        } else {
            return getShort(index) & 0xFFFF | (getShort(index + 2) & 0xFFFF) << 16;
        }
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putInt(int index, int value) {
		checkIndex(index, 1<<2);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
	    if (index + 4 <= endOffset) {
            c.getMemory().putInt(index - baseOffset, value);
        } else if (this.bigEndian) {
            putShort(index, (short) (value >>> 16));
            putShort(index + 2, (short) value);
        } else {
        	putShort(index, (short) value);
        	putShort(index + 2, (short) (value >>> 16));
        }
	    return this;
	}

	@Override
	public long getLong() {
		return getLong(nextGetIndex(1 << 3));
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putLong(long value) {
		return putLong(nextPutIndex(1 << 3), value);
	}

	@Override
	public long getLong(int index) {
		checkIndex(index, 1<<3);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
		if (index + 8 <= endOffset) {
			return c.getMemory().getLong(index - baseOffset);
		} else if (this.bigEndian) {
		     return (getInt(index) & 0xffffffffL) << 32 | getInt(index + 4) & 0xffffffffL;
        } else {
            return getInt(index) & 0xFFFFFFFFL | (getInt(index + 4) & 0xFFFFFFFFL) << 32;
        }
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putLong(int index, long value) {
		checkIndex(index, 1<<3);
		int chunkIndex = findChunkIndex(index);
		Chunk<ByteBuffer> c = chunkList.get(chunkIndex); // index所在的chunk
		int baseOffset = getChunkBaseOffset(chunkIndex);
		int endOffset = getChunkEndOffset(chunkIndex, baseOffset);
	    if (index + 8 <= endOffset) {
            c.getMemory().putLong(index - baseOffset, value);
        } else if (this.bigEndian) {
            putInt(index, (int) (value >>> 32));
            putInt(index + 4, (int) value);
        } else {
        	putInt(index, (int) value);
        	putInt(index + 4, (int) (value >>> 32));
        }
	    return this;
	}

	@Override
	   public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    } 
	
	@Override
	public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

	@Override
	public BaseByteBuffer<ByteBuffer> putFloat(float value) {
		return putFloat(nextPutIndex(1 << 2));
	}


	@Override
	public BaseByteBuffer<ByteBuffer> putFloat(int index, float value) {
		return putInt(index, Float.floatToRawIntBits(value));
	}

	@Override
	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putDouble(double value) {
		return putLong( Double.doubleToRawLongBits(value));
	}

	@Override
	public double getDouble(int index) {
		return Double.longBitsToDouble(getLong(index));
	}

	@Override
	public BaseByteBuffer<ByteBuffer> putDouble(int index, double value) {
		return putLong(index, Double.doubleToRawLongBits(value));
	}

	@Override
	public ByteBuffer compact() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReferenceCounted retain() {
		return null;
	}

	@Override
	public ReferenceCounted retain(int increment) {
		return null;
	}

	@Override
	public ReferenceCounted touch() {
		return null;
	}

	@Override
	public ReferenceCounted touch(Object hint) {
		return null;
	}

	@Override
	public boolean release() {
		return false;
	}

	@Override
	public boolean release(int decrement) {
		return false;
	}

	@Override
	public int refCnt() {
		return 0;
	}
}
