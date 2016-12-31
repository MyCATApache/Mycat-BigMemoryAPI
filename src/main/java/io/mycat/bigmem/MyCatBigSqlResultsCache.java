package io.mycat.bigmem;

import io.mycat.bigmem.console.LocatePolicy;
import io.mycat.bigmem.sqlcache.*;

/**
 * Cache SQL 大结果集 对外接口
 *
 * @author zagnix
 * @version 1.0
 * @create 2016-12-30 10:51
 */

public class MyCatBigSqlResultsCache {

    private final CacheImp<String,BigSQLResult> sqlResultCacheImp;

    private final static MyCatBigSqlResultsCache INSTANCE = new MyCatBigSqlResultsCache();

    private MyCatBigSqlResultsCache(){
        sqlResultCacheImp = new CacheImp<String,BigSQLResult>();
    }

    /**
     * 将sql结果集缓存起来
     *
     * @param sql  sql语句
     * @param bigSQLResult  sql结果集缓存
     * @param cache 缓存时间
     * @param accesCount  在缓存时间内，被访问的次数
     * @param loader  结果集load or reload 接口
     * @param listener key被移除时，调用的接口
     */

    public void cacheSQLResult(String sql, BigSQLResult bigSQLResult, long cache, long accesCount,
                                IDataLoader<String,BigSQLResult> loader, IRemoveKeyListener<String,BigSQLResult> listener){
        /**
         * TODO
         */
        String key = "" + sql;
        Keyer<String,BigSQLResult> keyer = new Keyer<String,BigSQLResult>();
        sqlResultCacheImp.put(key,bigSQLResult,keyer);
    }


    /**
     * 获取sql语句，已经缓存的结果集
     *
     * @param sql  sql 语句
     * @return
     */
    public BigSQLResult getSQLResult(String sql){
        /**
         * TODO
         */
        String key = "" + sql;
        return sqlResultCacheImp.get(key);
    }


    /**
     * 对外对象实例
     *
     * @return
     */
    public static MyCatBigSqlResultsCache getInstance() {
        return INSTANCE;
    }


    public static void main(String [] args){

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
                     * 根据sql，异步从后台DBreload数据，替换旧值
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
                     * key 失效，做清理工作
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
        BigSQLResult bigSQLResult =
                MyCatBigSqlResultsCache.getInstance().getSQLResult(sql);

        bigSQLResult.reset();
        while (bigSQLResult.hasNext()){
            byte[] data = bigSQLResult.next();
            //TODO
        }
    }


}
