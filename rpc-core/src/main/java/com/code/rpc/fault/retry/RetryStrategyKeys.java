package com.code.rpc.fault.retry;

/**
 * 重试策略键名常量
 *
 * @author Liang
 * @create 2024/3/29
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO = "no";

    /**
     * 固定时间间隔
     */
    String FIXED_INTERVAL = "fixedInterval";

    /**
     * 指数退避
     */
    String EXPONENTIAL_BACKOFF = "exponentialBackoff";

    /**
     * 斐波那契时间间隔
     */
    String FIBONACCI_INTERVAL = "fibonacciInterval";

    /**
     * 随机延迟
     */
    String RANDOM_DELAY = "randomDelay";

    /**
     * 复合等待时间间隔
     */
    String COMPOSITE = "composite";
}
