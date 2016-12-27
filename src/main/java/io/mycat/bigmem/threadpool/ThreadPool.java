package io.mycat.bigmem.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    /**
     * 线程池对象
    * @字段说明 POOLEXECUTOR
    */
    private ExecutorService POOLEXECUTOR = Executors.newFixedThreadPool(10);

    private static final ThreadPool POOL_INSTANCE = new ThreadPool();

    public static ThreadPool Instance() {
        return POOL_INSTANCE;
    }

    /**
     * 提交一个无返回值的任务
    * 方法描述
    * @param runjob
    * @创建日期 2016年12月26日
    */
    public void submit(Runnable runjob) {
        POOLEXECUTOR.submit(runjob);
    }

    /**
     * 提交一个带返回值的任务
    * 方法描述
    * @param callJob
    * @创建日期 2016年12月26日
    */
    public <T> void submit(Callable<T> callJob) {
        POOLEXECUTOR.submit(callJob);
    }

}
