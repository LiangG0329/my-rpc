package com.code.rpc.fault.retry;

import com.code.rpc.spi.SpiLoader;

/**
 * 重试策略工厂
 *
 * @author Liang
 * @create 2024/3/29
 */
public class RetryStrategyFactory {

    /**
     * 是否完成 SPI 加载
     */
    private static volatile boolean isRetryStrategySpiLoaded = false;

    /**
     * 默认重试器
     */
    private static final RetryStrategy DEFAULT_RETRY_FACTORY = new NoRetryStrategy();

    /**
     * 获取指定的重试器实例
     *
     * @param key 键
     * @return 重试器实例
     */
    public static RetryStrategy getInstance(String key) {
        if (!isRetryStrategySpiLoaded) {
            synchronized (RetryStrategyFactory.class) {
                if (!isRetryStrategySpiLoaded) {
                    SpiLoader.load(RetryStrategy.class);
                    isRetryStrategySpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
