package com.lest.common.redis.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 * <p>
 * 基于 Redisson 实现的分布式锁，替代手写 Redis SETNX 锁。
 * 提供公平锁、可重入锁、读锁、写锁等能力。
 * </p>
 * <p>
 * 用法示例：
 * <pre>{@code
 * // 简单用法
 * boolean locked = DistributedLockUtils.tryLock("my-lock", 10, TimeUnit.SECONDS);
 * try {
 *     // 临界区代码
 * } finally {
 *     DistributedLockUtils.unlock("my-lock");
 * }
 *
 * // Lambda 用法（自动释放）
 * String result = DistributedLockUtils.executeWithLock("my-lock", 10, TimeUnit.SECONDS, () -> {
 *     return computeResult();
 * });
 * }</pre>
 * </p>
 */
public class DistributedLockUtils {

    private static RedissonClient redissonClient;

    public static void setRedissonClient(RedissonClient client) {
        redissonClient = client;
    }

    private DistributedLockUtils() {}

    // ============================================================
    // 基础锁操作
    // ============================================================

    /**
     * 尝试获取锁（可重入）
     *
     * @param lockKey  锁的 key
     * @param waitTime 最大等待时间
     * @param unit     时间单位
     * @return true=获取成功，false=获取失败
     */
    public static boolean tryLock(String lockKey, long waitTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 尝试获取锁（可重入，带自动释放时间）
     *
     * @param lockKey  锁的 key
     * @param waitTime 最大等待时间
     * @param leaseTime 自动释放时间（防止死锁）
     * @param unit     时间单位
     * @return true=获取成功，false=获取失败
     */
    public static boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     */
    public static void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 判断锁是否被持有
     */
    public static boolean isLocked(String lockKey) {
        return getLock(lockKey).isLocked();
    }

    /**
     * 获取当前线程是否持有该锁
     */
    public static boolean isHeldByCurrentThread(String lockKey) {
        return getLock(lockKey).isHeldByCurrentThread();
    }

    // ============================================================
    // Lambda 安全执行（自动加锁/释放）
    // ============================================================

    /**
     * 在锁保护下执行任务，失败自动释放锁
     *
     * @param lockKey   锁 key
     * @param waitTime  最大等待时间
     * @param unit      时间单位
     * @param supplier  要执行的任务
     * @return 任务返回值
     * @throws LockAcquireException 无法获取锁时抛出
     */
    public static <T> T executeWithLock(String lockKey, long waitTime, TimeUnit unit,
                                          Supplier<T> supplier) {
        if (!tryLock(lockKey, waitTime, unit)) {
            throw new LockAcquireException("无法获取分布式锁: " + lockKey);
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 在锁保护下执行任务（带自动释放时间）
     */
    public static <T> T executeWithLock(String lockKey, long waitTime, long leaseTime,
                                          TimeUnit unit, Supplier<T> supplier) {
        if (!tryLock(lockKey, waitTime, leaseTime, unit)) {
            throw new LockAcquireException("无法获取分布式锁: " + lockKey);
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 在锁保护下执行无返回值任务
     */
    public static void runWithLock(String lockKey, long waitTime, TimeUnit unit, Runnable runnable) {
        executeWithLock(lockKey, waitTime, unit, () -> {
            runnable.run();
            return null;
        });
    }

    private static RLock getLock(String lockKey) {
        if (redissonClient == null) {
            throw new IllegalStateException("RedissonClient 未初始化，请先调用 setRedissonClient()");
        }
        return redissonClient.getLock(lockKey);
    }

    /**
     * 获取 Redisson 客户端（高级用法）
     */
    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }

    // ============================================================
    // 常用业务锁前缀
    // ============================================================

    public static String lockKey(String category, Long id) {
        return "lock:" + category + ":" + id;
    }

    public static String projectLock(Long projectId) {
        return lockKey("project", projectId);
    }

    public static String taskLock(Long taskId) {
        return lockKey("task", taskId);
    }

    public static String userLock(Long userId) {
        return lockKey("user", userId);
    }

    public static String releaseLock(Long releaseId) {
        return lockKey("release", releaseId);
    }

    // ============================================================
    // 异常
    // ============================================================

    public static class LockAcquireException extends RuntimeException {
        public LockAcquireException(String message) {
            super(message);
        }
    }
}
