package io.mycat.bigmem.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sun.misc.Unsafe;

public class ByteUtil {
	//public static Unsafe UnsafeUtil = getUnsafe();
	
	public static boolean UNALIGNED = UnsafeUtil.getUnaligned();
    private static final boolean BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
    private static Unsafe UNSAFE = UnsafeUtil.getUnsafe();
    
    
    public static void main(String[] args) {
		
	}
    
    public static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }
    
    public static char makeChar(byte b1, byte b0) {
        return (char)((b1 << 8) | (b0 & 0xff));
    }
    
    public static short getShort(long address) {
        if (UNALIGNED) {
            return UNSAFE.getShort(address);
        } else if (BIG_ENDIAN) {
            return (short) (getByte(address) << 8 | getByte(address + 1) & 0xff);
        } else {
            return (short) (getByte(address + 1) << 8 | getByte(address) & 0xff);
        }
    }

    public static int getInt(long address) {
        if (UNALIGNED) {
            return UNSAFE.getInt(address);
        } else if (BIG_ENDIAN) {
            return getByte(address) << 24 |
                  (getByte(address + 1) & 0xff) << 16 |
                  (getByte(address + 2) & 0xff) <<  8 |
                   getByte(address + 3) & 0xff;
        } else {
            return getByte(address + 3) << 24 |
                  (getByte(address + 2) & 0xff) << 16 |
                  (getByte(address + 1) & 0xff) <<  8 |
                   getByte(address) & 0xff;
        }
    }

    public static long getLong(long address) {
        if (UNALIGNED) {
            return UNSAFE.getLong(address);
        } else if (BIG_ENDIAN) {
            return (long) getByte(address) << 56 |
                  ((long) getByte(address + 1) & 0xff) << 48 |
                  ((long) getByte(address + 2) & 0xff) << 40 |
                  ((long) getByte(address + 3) & 0xff) << 32 |
                  ((long) getByte(address + 4) & 0xff) << 24 |
                  ((long) getByte(address + 5) & 0xff) << 16 |
                  ((long) getByte(address + 6) & 0xff) <<  8 |
                   (long) getByte(address + 7) & 0xff;
        } else {
            return (long) getByte(address + 7) << 56 |
                  ((long) getByte(address + 6) & 0xff) << 48 |
                  ((long) getByte(address + 5) & 0xff) << 40 |
                  ((long) getByte(address + 4) & 0xff) << 32 |
                  ((long) getByte(address + 3) & 0xff) << 24 |
                  ((long) getByte(address + 2) & 0xff) << 16 |
                  ((long) getByte(address + 1) & 0xff) <<  8 |
                   (long) getByte(address) & 0xff;
        }
    }

    public static void putOrderedObject(Object object, long address, Object value) {
        UNSAFE.putOrderedObject(object, address, value);
    }

    public static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    public static void putShort(long address, short value) {
        if (UNALIGNED) {
            UNSAFE.putShort(address, value);
        } else if (BIG_ENDIAN) {
            putByte(address, (byte) (value >>> 8));
            putByte(address + 1, (byte) value);
        } else {
            putByte(address + 1, (byte) (value >>> 8));
            putByte(address, (byte) value);
        }
    }

    public static void putInt(long address, int value) {
        if (UNALIGNED) {
            UNSAFE.putInt(address, value);
        } else if (BIG_ENDIAN) {
            putByte(address, (byte) (value >>> 24));
            putByte(address + 1, (byte) (value >>> 16));
            putByte(address + 2, (byte) (value >>> 8));
            putByte(address + 3, (byte) value);
        } else {
            putByte(address + 3, (byte) (value >>> 24));
            putByte(address + 2, (byte) (value >>> 16));
            putByte(address + 1, (byte) (value >>> 8));
            putByte(address, (byte) value);
        }
    }

    public static void putLong(long address, long value) {
        if (UNALIGNED) {
            UNSAFE.putLong(address, value);
        } else if (BIG_ENDIAN) {
            putByte(address, (byte) (value >>> 56));
            putByte(address + 1, (byte) (value >>> 48));
            putByte(address + 2, (byte) (value >>> 40));
            putByte(address + 3, (byte) (value >>> 32));
            putByte(address + 4, (byte) (value >>> 24));
            putByte(address + 5, (byte) (value >>> 16));
            putByte(address + 6, (byte) (value >>> 8));
            putByte(address + 7, (byte) value);
        } else {
            putByte(address + 7, (byte) (value >>> 56));
            putByte(address + 6, (byte) (value >>> 48));
            putByte(address + 5, (byte) (value >>> 40));
            putByte(address + 4, (byte) (value >>> 32));
            putByte(address + 3, (byte) (value >>> 24));
            putByte(address + 2, (byte) (value >>> 16));
            putByte(address + 1, (byte) (value >>> 8));
            putByte(address, (byte) value);
        }
    }
	
	public static void copyMemory(long srcAddress, long destAddress, long bytes) {
		UnsafeUtil.copyMemory(srcAddress, destAddress, bytes);
		
	}
	
	public static int addressSize() {
        return UNSAFE.addressSize();
    }

	public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

	public static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }	
	
}
