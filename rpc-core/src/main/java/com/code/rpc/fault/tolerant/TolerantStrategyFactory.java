package com.code.rpc.fault.tolerant;

import com.code.rpc.spi.SpiLoader;

/**
 * 容错策略工厂
 *
 * @author Liang
 * @create 2024/3/29
 */
public class TolerantStrategyFactory {

    /**
     * 是否完成 SPI 加载
     */
    private static volatile boolean isTolerantStrategySpiLoaded = false;

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取指定容错策略实例实例
     * @param key 键
     * @return 容错策略实例
     */
    public static TolerantStrategy getInstance(String key) {
        if (!isTolerantStrategySpiLoaded) {
            synchronized (TolerantStrategyFactory.class) {
                if (!isTolerantStrategySpiLoaded) {
                    SpiLoader.load(TolerantStrategy.class);
                    isTolerantStrategySpiLoaded = true;
                }
            }
        }
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
