package com.lest.common.core.utils;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.*;

/**
 * 线程工具类
 *
 * @author yshan2028
 */
public class ThreadUtils {

    private static final ExecutorService COMMON_POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            1000L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            r -> {
                Thread t = new Thread(r, "lest-pool-" + System.currentTimeMillis());
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private ThreadUtils() {
    }

    /**
     * 执行异步任务（无返回值）
     */
    public static void execute(Runnable runnable) {
        COMMON_POOL.execute(runnable);
    }

    /**
     * 执行异步任务（有返回值）
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return COMMON_POOL.submit(task);
    }

    /**
     * 执行异步任务（无返回值）
     */
    public static <T> Future<T> submit(Runnable task, T result) {
        return COMMON_POOL.submit(task, result);
    }

    /**
     * 睡眠指定毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static void sleep(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 获取当前线程 ID
     */
    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    /**
     * 获取当前线程名称
     */
    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 判断当前线程是否被中断
     */
    public static boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * 生成一个随机数字字符串
     */
    public static String randomNumeric(int length) {
        return RandomUtil.randomNumbers(length);
    }

    /**
     * 获取 ExecutorService（通用线程池）
     */
    public static ExecutorService getCommonPool() {
        return COMMON_POOL;
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        COMMON_POOL.shutdown();
    }

    /**
     * 优雅关闭线程池（等待任务完成）
     */
    public static void shutdownGracefully() {
        COMMON_POOL.shutdown();
        try {
            if (!COMMON_POOL.awaitTermination(60, TimeUnit.SECONDS)) {
                COMMON_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            COMMON_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
