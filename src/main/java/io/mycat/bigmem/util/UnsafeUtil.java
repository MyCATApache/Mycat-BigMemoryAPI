package io.mycat.bigmem.util;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

public final class UnsafeUtil {

	final static Logger LOGGER = LoggerFactory.getLogger(UnsafeUtil.class);

	private static final Unsafe theUnsafe = tryUnsafe();

	private static final VMDetail vmDetail = new VMDetail();
	
	  private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile(
	            "\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");

	private static Unsafe tryUnsafe() {
		return AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
			public Unsafe run() {
				try {
					Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
					unsafe.setAccessible(true);
					return (Unsafe) unsafe.get(null);
				} catch (NoSuchFieldException e) {
					throw new IllegalStateException(e);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		});
	}

	public static Unsafe getUnsafe() {
		return theUnsafe;
	}
	
	public boolean hasUnsafe()
	{
		return theUnsafe != null;
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static long addressOf(Object o) {
		return vmDetail.addressOf(o);
	}

	/**
	 * @return size of object point
	 */
	public static int getOopSize() {
		return vmDetail.getOopSize();
	}

	public static int getObjectHeaderSize() {
		return vmDetail.getObjectHeaderSize();
	}

	/**
	 * @return size of object point
	 */
	public static boolean compressedOopsEnabled() {
		return vmDetail.getCompressedOopsEnabled();
	}

	public static int getArrayBaseOffset() {
		return vmDetail.getArrayBaseOffset();
	}

	public static boolean unaligned = getUnaligned();

	public static AtomicLong unsafeSize = new AtomicLong(0);

	public static boolean getUnaligned() {
		String arch = AccessController
				.doPrivileged(new sun.security.action.GetPropertyAction(
						"os.arch"));
		unaligned = arch.equals("i386") || arch.equals("x86")
				|| arch.equals("amd64") || arch.equals("x86_64");
		return unaligned;
	}

	public static void putByte(long address, byte value) {
		theUnsafe.putByte(address, value);
	}

	public static byte getByte(long address) {
		return theUnsafe.getByte(address);
	}

	public static boolean getBoolean(long address) {
		return theUnsafe.getByte(address) == (byte) 1;
	}

	public static void putBoolean(long address, boolean value) {
		theUnsafe.putByte(address, value ? (byte) 1 : (byte) 0);
	}

	public static void putShort(long address, short value) {
		theUnsafe.putShort(address, value);
	}

	public static short getShort(long address) {
		return theUnsafe.getShort(address);
	}

	public static void putInt(long address, int value) {
		theUnsafe.putInt(address, value);
	}

	public static int getInt(long address) {
		return theUnsafe.getInt(address);
	}

	public static void putFloat(long address, float value) {
		theUnsafe.putFloat(address, value);
	}

	public static float getFloat(long address) {
		return theUnsafe.getFloat(address);
	}

	public static void putDouble(long address, double value) {
		theUnsafe.putDouble(address, value);
	}

	public static double getDouble(long address) {
		return theUnsafe.getDouble(address);
	}

	public static void putLong(long address, long value) {
		theUnsafe.putLong(address, value);
	}

	public static long getLong(long address) {
		return theUnsafe.getLong(address);
	}

	public static void copyMemory(long srcAddress, long destAddress, long bytes) {
		theUnsafe.copyMemory(srcAddress, destAddress, bytes);

	}

	public static long allocateMemory(long size) {
		return theUnsafe.allocateMemory(size);
	}

	public static void freeMemory(long address) {
		theUnsafe.freeMemory(address);
	}
	
    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

                public ClassLoader run() {
                    return ClassLoader.getSystemClassLoader();
                }
            });
        }
    }
    
    /**
     * 
     * @return 
     */
	public static long maxDirectMemory() {
	        long maxDirectMemory = 0;
	        ClassLoader systemClassLoader = null;
	        try {
	            // Try to get from sun.misc.VM.maxDirectMemory() which should be most accurate.
	            systemClassLoader = getSystemClassLoader();
	            Class<?> vmClass = Class.forName("sun.misc.VM", true, systemClassLoader);
	            Method m = vmClass.getDeclaredMethod("maxDirectMemory");
	            maxDirectMemory = ((Number) m.invoke(null)).longValue();
	        } catch (Throwable ignored) {
	            // Ignore
	        }

	        if (maxDirectMemory > 0) {
	            return maxDirectMemory;
	        }

	        try {
	        		String val = getVMOption("MaxDirectMemorySize");
	                Matcher m = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher(val);
	                if (m.matches()) {
	                maxDirectMemory = Long.parseLong(m.group(1));
	                switch (m.group(2).charAt(0)) {
	                    case 'k': case 'K':
	                        maxDirectMemory *= 1024;
	                        break;
	                    case 'm': case 'M':
	                        maxDirectMemory *= 1024 * 1024;
	                        break;
	                    case 'g': case 'G':
	                        maxDirectMemory *= 1024 * 1024 * 1024;
	                        break;
	                }
	            }
	        } catch (Throwable ignored) {
	            // Ignore
	        }

	        if (maxDirectMemory <= 0) {
	            maxDirectMemory = Runtime.getRuntime().maxMemory();
	            LOGGER.debug("maxDirectMemory: {} bytes (maybe)", maxDirectMemory);
	        } else {
	        	LOGGER.debug("maxDirectMemory: {} bytes", maxDirectMemory);
	        }

	        return maxDirectMemory;
	    }
	
	public static String getVMOption(String key) throws Exception {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		ObjectName mbean = new ObjectName(
				"com.sun.management:type=HotSpotDiagnostic");
		CompositeDataSupport val = (CompositeDataSupport) server.invoke(
				mbean, "getVMOption", new Object[] { key },
				new String[] { "java.lang.String" });
		return val.get("value").toString();
	}


	private static class VMDetail {
		private final int objectHeaderSize;
		private final int addressSize;
		private final int oopSize;
		private final int arrayBaseOffset;
		private final boolean compressedOopsEnabled;

		private final ThreadLocal<Object[]> LOCAL = new ThreadLocal<Object[]>() {
			@Override
			protected Object[] initialValue() {
				return new Object[1];
			}
		};

		public VMDetail() {
			this.oopSize = guessOopSize();
			this.addressSize = theUnsafe.addressSize();
			this.arrayBaseOffset = theUnsafe.arrayBaseOffset(Object[].class);
			this.objectHeaderSize = guessHeaderSize();
			Boolean coops = useCompressedOops();
			if (coops != null) {
				compressedOopsEnabled = coops;
			} else {
				compressedOopsEnabled = (addressSize != oopSize);
			}
		}

		public int getOopSize() {
			return this.oopSize;
		}

		public int getObjectHeaderSize() {
			return this.objectHeaderSize;
		}

		public int getArrayBaseOffset() {
			return this.arrayBaseOffset;
		}

		public boolean getCompressedOopsEnabled() {
			return this.compressedOopsEnabled;
		}

		private int guessHeaderSize() {
			try {
				long off1 = theUnsafe.objectFieldOffset(HeaderClass.class
						.getField("b1"));
				return (int) off1;
			} catch (NoSuchFieldException e) {
				return 0;
			}
		}

		private int guessOopSize() {
			// When running with CompressedOops on 64-bit platform, the address
			// size
			// reported by Unsafe is still 8, while the real reference fields
			// are 4 bytes long.
			// Try to guess the reference field size with this naive trick.
			int oopSize;
			try {
				long off1 = theUnsafe.objectFieldOffset(OopClass.class
						.getField("obj1"));
				long off2 = theUnsafe.objectFieldOffset(OopClass.class
						.getField("obj2"));
				oopSize = (int) Math.abs(off2 - off1);
			} catch (NoSuchFieldException e) {
				throw new IllegalStateException("Infrastructure failure", e);
			}
			return oopSize;
		}

		public long addressOf(Object o) {
			Object[] array = LOCAL.get();

			array[0] = o;

			long objectAddress;
			switch (oopSize) {
			case 4:
				objectAddress = theUnsafe.getInt(array, arrayBaseOffset) & 0xFFFFFFFFL;
				break;
			case 8:
				objectAddress = theUnsafe.getLong(array, arrayBaseOffset);
				break;
			default:
				throw new Error("unsupported address size: " + oopSize);
			}

			array[0] = null;

			return objectAddress;
		}



		public Boolean useCompressedOops() {
			try {
				return Boolean.valueOf(getVMOption("UseCompressedOops"));
			} catch (Exception exp) {
				LOGGER.error(exp.getMessage(), exp);
				return null;
			}
		}

		public Integer objectAlignmentInBytes() {
			if (Boolean.TRUE.equals(useCompressedOops())) {
				try {
					return Integer.valueOf(getVMOption("ObjectAlignmentInBytes"));
				} catch (Exception exp) {
					LOGGER.error(exp.getMessage(), exp);
					return null;
				}
			}
			return null;
		}

	}

	static class OopClass {
		public Object obj1;
		public Object obj2;
	}

	public static class HeaderClass {
		public boolean b1;
	}
}
