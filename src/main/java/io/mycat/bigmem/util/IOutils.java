package io.mycat.bigmem.util;

import java.io.Closeable;
import java.io.IOException;

public class IOutils {
    /**
     * 关闭流
     * @param stream
     */
    public static void closeStream(Closeable stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
