package io.mycat.bigmem.sqlcache;


import io.mycat.bigmem.console.LocatePolicy;
import io.mycat.bigmem.util.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


    @Test
    public void testSQLResultCache(){
        long ROWS = 10000;
        //long ROWS = 10000*10000;
        //long ROWS = 100000*100000;
        Map<String,BigSQLResultCache> sqlResultCacheMap = new HashMap<String,BigSQLResultCache>();

        String sql = "select * from table1";

        /**
         * sql results back list
         */

        ArrayList<byte[]> backList = new ArrayList<byte[]>();


        /**
         * 使用内存映射Cache，存放SQL结果集
         */

        BigSQLResultCache sqlResultCache
                = new BigSQLResultCache(LocatePolicy.Normal,sql,16*1024*1024);

        for (int i = 0; i < ROWS ; i++) {
            byte[] rows = Utils.randomString(1024).getBytes();
            backList.add(rows);

            /**
             * 使用内存映射Cache，存放SQL结果集
             */
            sqlResultCache.put(rows);
        }
        sqlResultCacheMap.put(sql,sqlResultCache);



        /**
         * 验证内存映射Cache，存放SQL结果集
         */
        BigSQLResultCache sqlResCache = sqlResultCacheMap.get(sql);
        Assert.assertEquals(backList.size(),sqlResCache.size());
        for (int i = 0; i <backList.size() ; i++) {
            if (sqlResultCache.hasNext()){
                Assert.assertArrayEquals(backList.get(i),sqlResCache.next());
            }
        }


        /**
         * 重复读
         */
        sqlResCache.reset();
        for (int i = 0; i <backList.size() ; i++) {
            if (sqlResultCache.hasNext()){
                Assert.assertArrayEquals(backList.get(i),sqlResCache.next());
            }
        }



        if (sqlResultCache !=null){
            sqlResultCache.removeAll();
        }

    }

    @After
    public void clean() throws IOException {
        if (sqlResultCache != null) {
            sqlResultCache.removeAll();
        }
    }


}
