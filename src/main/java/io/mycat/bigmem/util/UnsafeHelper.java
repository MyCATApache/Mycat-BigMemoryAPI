package io.mycat.bigmem.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import sun.misc.Unsafe;

public class UnsafeHelper {

    private static final Unsafe unsafe = createUnsafe();

    private static Unsafe createUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Can't use unsafe", e);
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    private static long roundUpTo8(final long number) {
        return ((number + 7) / 8) * 8;
    }

    /**
     * Returns the size of the header for an instance of this class (in bytes).
     *
     * <p>More information <a href="http://www.codeinstructions.com/2008/12/java-objects-memory-structure.html">http://www.codeinstructions.com/2008/12/java-objects-memory-structure.html</a>
     * and <a href="http://stackoverflow.com/a/17348396/88646">http://stackoverflow.com/a/17348396/88646</a>
     *
     * <p><pre>
     * ,------------------+------------------+------------------ +---------------.
     * |    mark word(8)  | klass pointer(4) |  array size (opt) |    padding    |
     * `------------------+------------------+-------------------+---------------'
     * </pre>
     *
     * @param clazz
     * @return
     */
    public static long headerSize(Class clazz) {
        // TODO Should be calculated based on the platform
        // TODO maybe unsafe.addressSize() would help?
        long len = 12; // JVM_64 has a 12 byte header 8 + 4 (with compressed
                       // pointers on)
        if (clazz.isArray()) {
            len += 4;
        }
        return len;
    }

    /**
     * Returns the offset of the first field in the range [headerSize, sizeOf].
     *
     * @param clazz
     * @return
     */
    public static long firstFieldOffset(Class clazz) {
        long minSize = roundUpTo8(headerSize(clazz));

        // Find the min offset for all the classes, up the class hierarchy.
        while (clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    long offset = unsafe.objectFieldOffset(f);
                    if (offset < minSize) {
                        minSize = offset;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        return minSize;
    }

    public static long sizeOf(Object obj) {
        Class clazz = obj.getClass();

        long len = sizeOf(clazz);

        if (clazz.isArray()) {
            // TODO Do extra work
            // TODO move into sizeof(Object)
            // (8) first longs and doubles; then
            // (4) ints and floats; then
            // (2) chars and shorts; then
            // (1) bytes and booleans, and last the
            // (4-8) references.
            Object[] array = (Object[]) obj;
            len += array.length * 8;
        }

        return len;
    }

    /**
     * Returns the size of an instance of this class (in bytes).
     * Instances include a header + all fields + padded to 8 bytes.
     * If this is an array, it does not include the size of the elements.
     *
     * @param clazz
     * @return
     */
    public static long sizeOf(Class clazz) {
        long maxSize = headerSize(clazz);

        while (clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if ((f.getModifiers() & Modifier.STATIC) == 0) {
                    long offset = unsafe.objectFieldOffset(f);
                    if (offset > maxSize) {
                        // Assume 1 byte of the field width. This is ok as it
                        // gets padded out at the end
                        maxSize = offset + 1;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        // The whole class always pads to a 8 bytes boundary, so we round up to
        // 8 bytes.
        return roundUpTo8(maxSize);
    }

}
