package io.mycat.bigmem.sqlcache;

import io.mycat.bigmem.MyCatBigSqlResultsCache;
import io.mycat.bigmem.console.LocatePolicy;
import org.junit.Test;

//TODO 待完善测试用例
public class testMyCatBigSqlResultsCache {

   @Test
   public void testBigSqlResultsCache(){

       String sql = "select * from table";

       BigSQLResult sqlResultCache =
               new BigSQLResult(LocatePolicy.Normal,sql,16*1024*1024);
       MyCatBigSqlResultsCache.getInstance().cacheSQLResult(sql, sqlResultCache, 300, 5000,
               new IDataLoader<String, BigSQLResult>() {

                   /**
                    * 根据sql，如果需要sql不存在，从后台DB拉数据
                    * @param key
                    * @return
                    */
                   @Override
                   public BigSQLResult load(String key) {
                       /**
                        * TODO
                        */
                       return null;
                   }

                   /**
                    * 根据sql，异步从后台DB    reload数据，替换旧值
                    * @param key
                    * @return
                    */
                   @Override
                   public BigSQLResult reload(String key) {
                       /**
                        * TODO
                        */
                       return null;
                   }
               }, new IRemoveKeyListener<String, BigSQLResult>() {

                   /**
                    *
                    * @param key
                    * @param value
                    */
                   @Override
                   public void removeNotify(String key, BigSQLResult value) {
                       if (value !=null){
                           value.removeAll();
                       }
                   }
               });


       /**
        * 访问Cache
        */

       BigSQLResult bigSQLResult = MyCatBigSqlResultsCache.getInstance().getSQLResult(sql);


       bigSQLResult.reset();
       while (bigSQLResult.hasNext()){
           byte[] data = bigSQLResult.next();
           //TODO
       }
   }
}
