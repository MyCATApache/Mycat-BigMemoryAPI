package io.mycat.bigmem.buffer;

public interface ByteBuffer<T> {

	public abstract long readerIndex();

	public abstract long writerIndex();

	public abstract byte getByte(long index);

	public abstract short getShort(long index);

	public abstract int getInt(long index);

	public abstract long getLong(long index);

	public abstract float getFloat(long index);

	public abstract double getDouble(long index);

	public abstract byte putByte(long index, byte value);

	public abstract byte putShort(long index, short value);

	public abstract byte putInt(long index, int value);

	public abstract byte putLong(long index, long value);

	public abstract byte putFloat(long index, float value);

	public abstract byte putDouble(long index, double value);

}