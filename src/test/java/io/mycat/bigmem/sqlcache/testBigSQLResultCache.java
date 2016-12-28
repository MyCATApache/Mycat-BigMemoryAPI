package io.mycat.bigmem.sqlcache;


import io.mycat.bigmem.console.LocatePolicy;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

//TODO
public class testBigSQLResultCache {
    private BigSQLResultCache sqlResultCache;

    @Test
    public void simpleTest() throws IOException {
        for(int i = 1; i <= 2; i++) {

            sqlResultCache = new BigSQLResultCache(LocatePolicy.Normal, "select * from t",16*1024*1024);
            assertNotNull(sqlResultCache);

            for(int j = 1; j <= 3; j++) {
                assertTrue(sqlResultCache.size() == 0L);
                assertTrue(sqlResultCache.isEmpty());

                assertNull(sqlResultCache.next());

                sqlResultCache.put("hello".getBytes());
                assertTrue(sqlResultCache.size() == 1L);
                assertTrue(sqlResultCache.hasNext());
                assertEquals("hello", new String(sqlResultCache.next()));
                assertNull(sqlResultCache.next());

                sqlResultCache.put("world".getBytes());
                sqlResultCache.flush();
                assertTrue(sqlResultCache.size() == 1L);
                assertTrue(sqlResultCache.hasNext());
                assertEquals("world", new String(sqlResultCache.next()));
                assertNull(sqlResultCache.next());

            }
            sqlResultCache.recycle();
        }
    }


    @After
    public void clean() throws IOException {
        if (sqlResultCache != null) {
            sqlResultCache.removeAll();
        }
    }


}
